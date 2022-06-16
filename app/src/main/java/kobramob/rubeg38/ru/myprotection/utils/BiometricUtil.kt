package kobramob.rubeg38.ru.myprotection.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

class BiometricUtil(private val context: Context) {

    fun isBiometricReady(): Boolean =
        BiometricManager.from(context).run {
            val result = canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            result == BiometricManager.BIOMETRIC_SUCCESS
        }

    fun showBiometricPrompt(
        title: String,
        subtitle: String = "",
        description: String = "",
        negativeButtonText: String,
        activity: AppCompatActivity,
        listener: (String?, Boolean) -> Unit
    ) {
        val promptInfo = getBiometricPromptInfo(
            title,
            subtitle,
            description,
            negativeButtonText
        )

        val biometricPrompt = initBiometricPrompt(activity, listener)

        biometricPrompt.authenticate(promptInfo)
    }

    private fun getBiometricPromptInfo(
        title: String,
        subtitle: String,
        description: String,
        negativeButtonText: String
    ): BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setNegativeButtonText(negativeButtonText)
            .build()

    private fun initBiometricPrompt(
        activity: AppCompatActivity,
        listener: (String?, Boolean) -> Unit
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(activity)


        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                listener(null, true)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                listener(null, false)
            }

            override fun onAuthenticationError(errorCode: Int, errorDescription: CharSequence) {
                super.onAuthenticationError(errorCode, errorDescription)
                listener(errorDescription.toString(), false)
            }
        }

        return BiometricPrompt(activity, executor, callback)
    }

}