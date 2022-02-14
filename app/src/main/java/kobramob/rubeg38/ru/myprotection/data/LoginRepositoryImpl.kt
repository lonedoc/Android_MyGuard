package kobramob.rubeg38.ru.myprotection.data

import kobramob.rubeg38.ru.myprotection.domain.models.Credentials
import kobramob.rubeg38.ru.myprotection.domain.models.LoginResponse
import kobramob.rubeg38.ru.myprotection.domain.toDomain

class LoginRepositoryImpl(private val loginApi: LoginApi) : LoginRepository {

    override suspend fun login(
        credentials: Credentials,
        fcmToken: String,
        deviceName: String
    ): LoginResponse {
        return loginApi.login(credentials, fcmToken, deviceName).toDomain()
    }

    override suspend fun logout(): Boolean {
        return loginApi.logout()
    }

}