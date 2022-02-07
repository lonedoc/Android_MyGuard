package kobramob.rubeg38.ru.myprotection.domain

import kobramob.rubeg38.ru.myprotection.data.LoginRepository
import kobramob.rubeg38.ru.myprotection.domain.models.Credentials
import kobramob.rubeg38.ru.myprotection.utils.attempt

private const val NO_COUNTRY_CODE_PHONE_NUMBER_SIZE = 10

class LoginInteractor(private val loginRepository: LoginRepository) {

    suspend fun login(credentials: Credentials, fcmToken: String, deviceName: String) = attempt {
        val preparedPhoneNumber = credentials.phoneNumber
            .filter { char -> char.isDigit() }
            .takeLast(NO_COUNTRY_CODE_PHONE_NUMBER_SIZE) // drop country code

        loginRepository.login(
            credentials.copy(phoneNumber = preparedPhoneNumber),
            fcmToken,
            deviceName
        )
    }

}