package kobramob.rubeg38.ru.myprotection.feature.login.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.databinding.FragmentLoginBinding
import kobramob.rubeg38.ru.myprotection.domain.models.GuardService
import kobramob.rubeg38.ru.myprotection.feature.login.domain.models.City
import kobramob.rubeg38.ru.myprotection.feature.login.ui.adapters.SingleLineTextAdapter
import kobramob.rubeg38.ru.myprotection.feature.login.ui.adapters.singleLineTextAdapter
import kobramob.rubeg38.ru.myprotection.utils.onItemSelectedListener
import kobramob.rubeg38.ru.myprotection.utils.setThrottledClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.tinkoff.decoro.FormattedTextChangeListener
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

class LoginFragment : Fragment(R.layout.fragment_login) {

    companion object {
        fun create(): LoginFragment = LoginFragment()
    }

    private val viewModel: LoginViewModel by viewModel()
    private val binding: FragmentLoginBinding by viewBinding()

    private val phoneNumberWatcher: FormatWatcher by lazy {
        val mask = MaskImpl(PredefinedSlots.RUS_PHONE_NUMBER, true)
        val formatWatcher = MaskFormatWatcher(mask)

        formatWatcher.setCallback(object : FormattedTextChangeListener {
            override fun beforeFormatting(oldValue: String?, newValue: String?): Boolean {
                return false
            }

            override fun onTextFormatted(formatter: FormatWatcher?, newFormattedText: String?) {
                viewModel.processUiEvent(UiEvent.OnPhoneNumberEntered(newFormattedText))
            }
        })

        formatWatcher
    }

    private val citiesAdapter: SingleLineTextAdapter<City> by lazy {
        singleLineTextAdapter(
            requireContext(),
            requireContext().getString(R.string.city_picker_hint),
            emptyList()
        ) { city -> city.name }
    }

    private val guardServicesAdapter: SingleLineTextAdapter<GuardService> by lazy {
        singleLineTextAdapter(
            requireContext(),
            requireContext().getString(R.string.guard_service_picker_hint),
            emptyList()
        ) { company -> company.name }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when(resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)){
            Configuration.UI_MODE_NIGHT_YES->{
                activity?.window?.statusBarColor = context?.getColor(R.color.darkthemebackgroundcolor)!!
            }
            Configuration.UI_MODE_NIGHT_NO->{
                activity?.window?.statusBarColor = context?.getColor(R.color.lightthemenostatusbarcolor)!!
            }
            else->{
                activity?.window?.statusBarColor = context?.getColor(R.color.darkthemebackgroundcolor)!!
            }
        }

        setupCityPicker()
        setupCompanyPicker()
        setupPhoneNumberMask()

        binding.submitButton.setThrottledClickListener {
            viewModel.processUiEvent(UiEvent.OnProceedButtonClicked)
        }

        binding.clearButton.setThrottledClickListener {
            binding.phoneEditText.setText("")
            phoneNumberWatcher.refreshMask()
        }

        viewModel.viewState.observe(viewLifecycleOwner, ::render)
        viewModel.singleEvent.observe(viewLifecycleOwner, ::handleSingleEvent)
    }

    private fun setupCityPicker() {
        binding.cityPicker.adapter = citiesAdapter

        binding.cityPicker.onItemSelectedListener = onItemSelectedListener { position ->
            citiesAdapter.setSelectedItemPosition(position)

            val isHint = position == 0

            if (isHint) {
                viewModel.processUiEvent(UiEvent.OnCitySelected(null))
            } else {
                citiesAdapter.getItem(position)?.let { selectedCity ->
                    viewModel.processUiEvent(UiEvent.OnCitySelected(selectedCity))
                }
            }
        }
    }

    private fun setupCompanyPicker() {
        binding.guardServicePicker.adapter = guardServicesAdapter

        binding.guardServicePicker.onItemSelectedListener = onItemSelectedListener { position ->
            guardServicesAdapter.setSelectedItemPosition(position)

            val isHint = position == 0

            if (isHint) {
                viewModel.processUiEvent(UiEvent.OnCompanySelected(null))
            } else {
                guardServicesAdapter.getItem(position)?.let { selectedCompany ->
                    viewModel.processUiEvent(UiEvent.OnCompanySelected(selectedCompany))
                }
            }
        }
    }

    private fun setupPhoneNumberMask() {
        phoneNumberWatcher.installOn(binding.phoneEditText)
    }


    private fun render(viewState: ViewState) {
        updatePicker(
            picker = binding.cityPicker,
            adapter = citiesAdapter,
            items = viewState.cities,
            selectedItem = viewState.selectedCity
        )

        updatePicker(
            picker = binding.guardServicePicker,
            adapter = guardServicesAdapter,
            items = viewState.guardServices,
            selectedItem = viewState.selectedGuardService
        )

        if (!binding.phoneEditText.hasFocus()) {
            binding.phoneEditText.setText(viewState.phoneNumber ?: "")
        }

        binding.submitButton.isEnabled = viewState.isSubmitButtonEnabled
    }

    private fun <T> updatePicker(
        picker: Spinner,
        adapter: ArrayAdapter<T>,
        items: List<T>,
        selectedItem: T?
    ) {
        adapter.clear()
        adapter.addAll(items)
        adapter.notifyDataSetChanged()

        if (selectedItem == null) {
            return
        }

        val position = items.indexOf(selectedItem)

        if (position < 0) {
            return
        }

        picker.setSelection(position + 1, true)
    }

    private fun handleSingleEvent(event: SingleEvent) {
        when (event) {
            is SingleEvent.OnShowErrorMessage -> {
                val errorMessage = getString(event.errorMessageRes)
                showErrorMessage(errorMessage)
            }
        }
    }

    private fun showErrorMessage(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

}