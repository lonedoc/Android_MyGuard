package kobramob.rubeg38.ru.myprotection.data

import kobramob.rubeg38.ru.myprotection.domain.models.Credentials
import kobramob.rubeg38.ru.myprotection.domain.models.LoginResponse

interface LoginRepository {
    suspend fun login(
        credentials: Credentials,
        fcmToken: String,
        deviceName: String
    ): LoginResponse

    suspend fun logout(): Boolean
}