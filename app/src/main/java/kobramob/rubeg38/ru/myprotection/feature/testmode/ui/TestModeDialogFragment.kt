package kobramob.rubeg38.ru.myprotection.feature.testmode.ui

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import kobramob.rubeg38.ru.myprotection.databinding.FragmentTestModeDialogBinding
import kobramob.rubeg38.ru.myprotection.utils.setThrottledClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class TestModeDialogFragment : DialogFragment() {

    companion object {
        private const val FACILITY_ID_KEY = "facilityId"

        fun create(facilityId: String) = TestModeDialogFragment().apply {
            arguments = bundleOf(FACILITY_ID_KEY to facilityId)
        }
    }

    private val viewModel: TestModeViewModel by viewModel {
        parametersOf(requireArguments().getString(FACILITY_ID_KEY) ?: "")
    }

    private var binding: FragmentTestModeDialogBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroy() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onDestroy()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val viewBinding = FragmentTestModeDialogBinding.inflate(layoutInflater)
        binding = viewBinding

        val dialog = AlertDialog.Builder(requireContext()).run {
            setView(viewBinding.root)
            setCancelable(false)
            create()
        }

        dialog.setCanceledOnTouchOutside(false)

        viewBinding.resetButton.setThrottledClickListener {
            viewModel.processUiEvent(UiEvent.OnResetButtonClick)
        }

        viewBinding.endTestingButton.setThrottledClickListener {
            viewModel.processUiEvent(UiEvent.OnEndTestingButtonClick)
        }

        lifecycle.addObserver(viewModel)

        viewModel.viewState.observe(this, ::render)
        viewModel.singleEvent.observe(this, ::handleSingleEvent)

        return dialog
    }

    private fun render(viewState: ViewState) {
        binding?.startTextView?.text = "Начало тестирования"
        binding?.hintTextView?.text = viewState.hintRes?.let { hintRes -> getString(hintRes) }
        binding?.countdownTextView?.text = viewState.countDownText
        binding?.resetButton?.isEnabled = viewState.isResetButtonEnabled
    }

    private fun handleSingleEvent(event: SingleEvent) {
        when (event) {
            is SingleEvent.OnDismissDialog -> {
                dismiss()
            }
            is SingleEvent.OnError -> {
                showErrorMessage(event.errorMessageRes)
            }
        }
    }

    private fun showErrorMessage(@StringRes errorMessageRes: Int) {
        Toast.makeText(requireContext(), errorMessageRes, Toast.LENGTH_LONG).show()
    }

}