package kobramob.rubeg38.ru.myprotection.feature.passcode.domain

import kobramob.rubeg38.ru.myprotection.data.LoginRepository
import kobramob.rubeg38.ru.myprotection.data.SharedPreferencesHelper
import kobramob.rubeg38.ru.myprotection.domain.models.GuardService
import kobramob.rubeg38.ru.myprotection.utils.BiometricUtil
import kobramob.rubeg38.ru.myprotection.utils.attempt

class PasscodeInteractor(
    private val loginRepository: LoginRepository,
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val biometricUtil: BiometricUtil
) {

    fun isBiometricReady(): Boolean = biometricUtil.isBiometricReady()

    fun getGuardService(): GuardService? = sharedPreferencesHelper.guardService

    fun getUserPhoneNumber(): String? = sharedPreferencesHelper.userPhoneNumber

    fun getGuardServicePhoneNumber(): String? = sharedPreferencesHelper.guardService?.phoneNumber

    fun getPasscode(): String? = sharedPreferencesHelper.passcode

    fun savePasscode(passcode: String) {
        sharedPreferencesHelper.passcode = passcode
    }

    fun removePasscode() {
        sharedPreferencesHelper.passcode = null
    }

    suspend fun logout() = attempt { loginRepository.logout() }

}