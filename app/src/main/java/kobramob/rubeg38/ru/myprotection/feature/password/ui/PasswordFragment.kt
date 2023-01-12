package kobramob.rubeg38.ru.myprotection.feature.password.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.databinding.FragmentPasswordBinding
import kobramob.rubeg38.ru.myprotection.domain.models.GuardService
import kobramob.rubeg38.ru.myprotection.utils.setDebouncingTextListener
import kobramob.rubeg38.ru.myprotection.utils.setThrottledClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val GOOGLE_SERVICES_ERROR_DIALOG_CODE = 9182

class PasswordFragment : Fragment(R.layout.fragment_password) {

    companion object {
        private const val GUARD_SERVICE_KEY = "guardService"
        private const val PHONE_NUMBER_KEY = "phoneNumber"

        fun create(guardService: GuardService, phoneNumber: String) = PasswordFragment().apply {
            arguments = bundleOf(
                GUARD_SERVICE_KEY to guardService,
                PHONE_NUMBER_KEY to phoneNumber
            )
        }
    }

    private val viewModel: PasswordViewModel by viewModel {
        val guardService = requireArguments().getParcelable<GuardService>(GUARD_SERVICE_KEY)!!
        val phoneNumber = requireArguments().getString(PHONE_NUMBER_KEY)!!
        parametersOf(guardService, phoneNumber)
    }

    private val binding: FragmentPasswordBinding by viewBinding()

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

        binding.passwordEditText.setDebouncingTextListener { value ->
            viewModel.processUiEvent(UiEvent.OnPasswordChange(value))
        }

        binding.retryButton.setThrottledClickListener {
            viewModel.processUiEvent(UiEvent.OnPasswordRequest)
        }

        binding.cancelButton.setThrottledClickListener {
            viewModel.processUiEvent(UiEvent.OnCancelButtonClick)
        }

        binding.submitButton.setThrottledClickListener {
            if (isGoogleServicesAvailable()) {
                getFcmToken { token ->
                    if (token == null) {
                        viewModel.processUiEvent(UiEvent.OnFcmTokenRequestFail)
                        return@getFcmToken
                    }

                    viewModel.processUiEvent(UiEvent.OnReadyToLogin(token))
                }
            }
        }

        viewModel.viewState.observe(viewLifecycleOwner, ::render)
        viewModel.singleEvent.observe(viewLifecycleOwner, ::handleEvent)
    }

    private fun render(viewState: ViewState) {
        val passwordCooldownText = getString(
            R.string.password_request_cooldown_text,
            viewState.countDownValue.toString()
        )

        binding.passwordCooldownTextView.text = passwordCooldownText
        binding.passwordCooldownTextView.isInvisible = !viewState.isCountDownShown
        binding.retryButton.isGone = viewState.isCountDownShown
        binding.submitButton.isEnabled = viewState.isSubmitButtonEnabled
    }

    private fun handleEvent(event: SingleEvent) {
        when (event) {
            is SingleEvent.OnError -> {
                showErrorMessage(event.errorMessageRes)
            }
        }
    }

    private fun isGoogleServicesAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(requireContext())

        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                val dialog = googleApiAvailability.getErrorDialog(
                    requireActivity(),
                    status,
                    GOOGLE_SERVICES_ERROR_DIALOG_CODE
                )

                dialog?.show()
            }

            return false
        }

        return true
    }

    private fun getFcmToken(onComplete: (String?) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                onComplete(null)
                return@addOnCompleteListener
            }

            val token = task.result

            onComplete(token)
        }
    }

    private fun showErrorMessage(@StringRes errorMessageRes: Int) {
        Toast.makeText(requireContext(), errorMessageRes, Toast.LENGTH_LONG).show()
    }

}