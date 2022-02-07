package kobramob.rubeg38.ru.myprotection.feature.password.data

interface PasswordRepository {
    suspend fun requestPassword(phoneNumber: String): Boolean
}