package kobramob.rubeg38.ru.myprotection.feature.applications.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.databinding.FragmentApplicationsBinding
import kobramob.rubeg38.ru.myprotection.utils.loadData
import kobramob.rubeg38.ru.myprotection.utils.setThrottledClickListener
import kobramob.rubeg38.ru.myprotection.utils.textChangedListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class ApplicationsFragment : Fragment(R.layout.fragment_applications) {

    companion object {
        private const val FACILITY_ID_KEY = "facility_id"

        fun create(facilityId: String) = ApplicationsFragment().apply {
            arguments = bundleOf(FACILITY_ID_KEY to facilityId)
        }
    }

    private val viewModel: ApplicationsViewModel by viewModel {
        val facilityId = requireArguments().getString(FACILITY_ID_KEY)
        parametersOf(facilityId)
    }

    private val binding: FragmentApplicationsBinding by viewBinding()

    private val applicationsAdapter: TextAdapter by lazy {
        textAdapter(requireContext(), emptyList())
    }

    private val applicationTextChangedListener = textChangedListener { applicationText ->
        viewModel.processUiEvent(UiEvent.OnApplicationTextChanged(applicationText))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appBar.setNavigationOnClickListener {
            viewModel.processUiEvent(UiEvent.OnCloseButtonClick)
        }

        setupApplicationPicker()

        binding.applicationTextField.addTextChangedListener(applicationTextChangedListener)

        binding.dateTextField.setOnClickListener {
            viewModel.processUiEvent(UiEvent.OnDateFieldClick)
        }

        binding.timeTextField.setOnClickListener {
            viewModel.processUiEvent(UiEvent.OnTimeFieldClick)
        }

        binding.submitButton.setThrottledClickListener {
            viewModel.processUiEvent(UiEvent.OnSubmitButtonClick)
        }

        viewModel.viewState.observe(viewLifecycleOwner, ::render)
        viewModel.singleEvent.observe(viewLifecycleOwner, ::handleSingleEvent)
    }

    private fun setupApplicationPicker() {
        binding.applicationsPicker.setAdapter(applicationsAdapter)

        binding.applicationsPicker.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                applicationsAdapter.setSelectedItemPosition(position)

                applicationsAdapter.getItem(position)?.let { selectedApplication ->
                    viewModel.processUiEvent(UiEvent.OnApplicationSelected(selectedApplication))
                }
            }
    }

    private fun render(viewState: ViewState) {
        applicationsAdapter.loadData(viewState.applications)

        if (viewState.applicationText != binding.applicationTextField.text.toString()) {
            binding.applicationTextField.removeTextChangedListener(applicationTextChangedListener)
            binding.applicationTextField.setText(viewState.applicationText)
            binding.applicationTextField.addTextChangedListener(applicationTextChangedListener)
        }

        binding.dateTextField.setText(viewState.dateText)
        binding.timeTextField.setText(viewState.timeText)

        binding.applicationTextField.isEnabled = viewState.isApplicationTextFieldEnabled
        binding.submitButton.isEnabled = viewState.isSubmitButtonEnabled
    }

    private fun handleSingleEvent(event: SingleEvent) {
        when (event) {
            is SingleEvent.OnDatePickerDialog -> {
                showDatePicker(event.initialValue)
            }
            is SingleEvent.OnTimePickerDialog -> {
                showTimePicker(event.initialValue)
            }
            is SingleEvent.OnError -> {
                showErrorMessage(event.errorMessage)
            }
        }
    }

    private fun showDatePicker(initialValue: Date) {
        val calendar = Calendar.getInstance().apply { time = initialValue }

        val onDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val date = calendar.run {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
                time
            }

            viewModel.processUiEvent(UiEvent.OnDateChanged(date))
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            onDateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = Date().time
        datePickerDialog.show()
    }

    private fun showTimePicker(initialValue: Date) {
        val calendar = Calendar.getInstance().apply { time = initialValue }

        val onTimeSetListener = TimePickerDialog.OnTimeSetListener { _, hours, minutes ->
            val date = calendar.run {
                set(Calendar.HOUR_OF_DAY, hours)
                set(Calendar.MINUTE, minutes)
                time
            }

            viewModel.processUiEvent(UiEvent.OnTimeChanged(date))
        }

        TimePickerDialog(
            requireContext(),
            onTimeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun showErrorMessage(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

}