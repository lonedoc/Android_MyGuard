package kobramob.rubeg38.ru.myprotection.feature.login.domain.models

import kobramob.rubeg38.ru.myprotection.domain.models.GuardService

data class City(
    val name: String,
    val guardServices: List<GuardService>
)