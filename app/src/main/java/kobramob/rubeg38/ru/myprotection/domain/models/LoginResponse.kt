package kobramob.rubeg38.ru.myprotection.domain.models

data class LoginResponse(
    val token: String,
    val user: User,
    val guardService: GuardService
)