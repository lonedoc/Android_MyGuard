package kobramob.rubeg38.ru.myprotection.domain

import kobramob.rubeg38.ru.myprotection.data.models.LoginResponseDto
import kobramob.rubeg38.ru.myprotection.domain.models.GuardService
import kobramob.rubeg38.ru.myprotection.domain.models.LoginResponse
import kobramob.rubeg38.ru.myprotection.domain.models.User

fun LoginResponseDto.toDomain() = LoginResponse(
    token = token,
    user = User(
        id = userId,
        name = userName
    ),
    guardService = GuardService(
        name = "",
        displayedName = guardServiceName,
        phoneNumber = guardServicePhoneNumber,
        addresses = emptyList()
    )
)