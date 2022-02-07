package kobramob.rubeg38.ru.myprotection.feature.password.data

class PasswordRepositoryImpl(private val passwordApi: PasswordApi) : PasswordRepository {

    override suspend fun requestPassword(phoneNumber: String): Boolean {
        return passwordApi.requestPassword(phoneNumber)
    }

}