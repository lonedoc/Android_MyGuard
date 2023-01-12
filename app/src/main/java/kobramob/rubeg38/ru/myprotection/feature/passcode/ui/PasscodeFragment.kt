package kobramob.rubeg38.ru.myprotection.feature.passcode.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.databinding.FragmentPasscodeBinding
import kobramob.rubeg38.ru.myprotection.utils.BiometricUtil
import kobramob.rubeg38.ru.myprotection.utils.setThrottledClickListener
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PasscodeFragment : Fragment(R.layout.fragment_passcode) {

    companion object {
        fun create() = PasscodeFragment()
    }

    private val viewModel: PasscodeViewModel by viewModel()
    private val binding: FragmentPasscodeBinding by viewBinding()
    private val biometricUtil: BiometricUtil by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setKeyboardListeners()

/*        binding.appBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.exitItem -> {
                    viewModel.processUiEvent(UiEvent.OnExitButtonClick)
                    true
                }
                R.id.callItem -> {
                    viewModel.processUiEvent(UiEvent.OnCallButtonClick)
                    true
                }
                else -> false
            }
        }*/

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


        binding.forgotPasscodeButton.setOnClickListener {
            viewModel.processUiEvent(UiEvent.OnForgotCodeButtonClick)
        }

        binding.keyboardView.biometryButton.setThrottledClickListener {
            viewModel.processUiEvent(UiEvent.OnBiometricButtonClick)
        }

        viewModel.viewState.observe(viewLifecycleOwner, ::render)
        viewModel.singleEvent.observe(viewLifecycleOwner, ::handleSingleEvent)
    }

    private fun render(viewState: ViewState) {
        binding.hintTextView.text = getString(viewState.hintTextRes)

        setIndicatorValue(viewState.indicatorValue)

       /* if (viewState.isAppBarMenuVisible && binding.appBar.menu.isEmpty()) {
            binding.appBar.inflateMenu(R.menu.passcode_appbar_menu)
        }*/

        binding.keyboardView.biometryButton.isVisible = viewState.isBiometricButtonVisible
        binding.forgotPasscodeButton.isVisible = viewState.isForgotCodeButtonVisible
        binding.versionTextView.text = viewState.version
    }

    private fun handleSingleEvent(event: SingleEvent) {
        when (event) {
            is SingleEvent.OnError -> {
                showErrorMessage(event.errorMessageRes)
            }
            is SingleEvent.OnCall -> {
                val intent = Intent(Intent.ACTION_DIAL, event.uri)
                startActivity(intent)
            }
            is SingleEvent.OnShowBiometricPrompt -> {
                showBiometricPrompt()
            }
        }
    }

    private fun showBiometricPrompt() {
        val title: String
        val negativeButtonText: String

        requireContext().run {
            title = getString(R.string.biometric_prompt_title)
            negativeButtonText = getString(R.string.cancel_button_text)
        }

        biometricUtil.showBiometricPrompt(
            title = title,
            negativeButtonText = negativeButtonText,
            activity = requireActivity() as AppCompatActivity
        ) callback@ { errorText, success ->
            if (success) {
                viewModel.processUiEvent(UiEvent.OnBiometricAuthSuccess)
                return@callback
            }

            val errorMessage =
                errorText ?:
                requireContext().getString(R.string.biometric_auth_failed_message)

            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun setKeyboardListeners() {
        binding.keyboardView.root.children.forEach { child ->
            (child.tag as? String)?.let { tag ->
                if (tag.count() == 1) {
                    child.setOnClickListener {
                        viewModel.processUiEvent(UiEvent.OnCharacterAdded(tag))
                    }
                }
            }
        }

        binding.keyboardView.backspaceButton.setOnClickListener {
            viewModel.processUiEvent(UiEvent.OnCharacterRemoved)
        }
    }

    private fun setIndicatorValue(value: Int) {
        val indicatorItems = listOf(
            binding.indicatorView1,
            binding.indicatorView2,
            binding.indicatorView3,
            binding.indicatorView4
        )

        indicatorItems.forEachIndexed { index, item ->
            val drawableRes = if (index < value) {
                R.drawable.ic_indicator_dot_blue
            } else {
                R.drawable.ic_indicator_dot_grey
            }

            item.setImageResource(drawableRes)
        }
    }

    private fun showErrorMessage(@StringRes errorMessageRes: Int) {
        Toast.makeText(requireContext(), errorMessageRes, Toast.LENGTH_LONG).show()
    }

}