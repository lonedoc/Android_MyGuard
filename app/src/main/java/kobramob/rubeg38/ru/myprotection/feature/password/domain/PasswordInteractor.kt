package kobramob.rubeg38.ru.myprotection.feature.password.domain

import kobramob.rubeg38.ru.myprotection.data.SharedPreferencesHelper
import kobramob.rubeg38.ru.myprotection.domain.models.GuardService
import kobramob.rubeg38.ru.myprotection.domain.models.User
import kobramob.rubeg38.ru.myprotection.feature.password.data.PasswordRepository
import kobramob.rubeg38.ru.myprotection.utils.SessionDataHolder
import kobramob.rubeg38.ru.myprotection.utils.attempt
import ru.rubeg38.protocolclient.Address

private const val NO_COUNTRY_CODE_PHONE_NUMBER_SIZE = 10
private const val PORT = 8301

class PasswordInteractor(
    private val sessionDataHolder: SessionDataHolder,
    private val passwordRepository: PasswordRepository,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) {

    suspend fun setIpAddresses(ipAddresses: List<String>) {
        val addresses = ipAddresses.mapNotNull { ipAddress ->
            try {
                Address.create(ipAddress, PORT)
            } catch(ex: IllegalArgumentException) {
                null
            }
        }

        sessionDataHolder.setAddresses(addresses)
    }

    suspend fun setToken(token: String) {
        sessionDataHolder.setToken(token)
    }

    suspend fun requestPassword(phoneNumber: String) = attempt {
        val preparedPhoneNumber = phoneNumber
            .filter { char -> char.isDigit() }
            .takeLast(NO_COUNTRY_CODE_PHONE_NUMBER_SIZE) // drop country code

        passwordRepository.requestPassword(preparedPhoneNumber)
    }

    fun saveToken(token: String) {
        sharedPreferencesHelper.token = token
    }

    fun saveUser(user: User) {
        sharedPreferencesHelper.user = user
    }

    fun updateGuardService(guardService: GuardService) {
        sharedPreferencesHelper.guardService?.let { cachedGuardService ->
            sharedPreferencesHelper.guardService = cachedGuardService.copy(
                displayedName = guardService.displayedName,
                phoneNumber = guardService.phoneNumber
            )
        }
    }

}
