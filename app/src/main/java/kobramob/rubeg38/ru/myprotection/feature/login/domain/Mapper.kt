package kobramob.rubeg38.ru.myprotection.feature.login.domain

import kobramob.rubeg38.ru.myprotection.domain.models.GuardService
import kobramob.rubeg38.ru.myprotection.feature.login.data.models.CityDto
import kobramob.rubeg38.ru.myprotection.feature.login.data.models.CitiesDataDto
import kobramob.rubeg38.ru.myprotection.feature.login.data.models.GuardServiceDto
import kobramob.rubeg38.ru.myprotection.feature.login.domain.models.City

fun CitiesDataDto.toDomain(): List<City> = this.cities.map(CityDto::toDomain)

fun CityDto.toDomain(): City = City(
    name = this.name,
    guardServices = this.guardServices.map(GuardServiceDto::toDomain)
)

fun GuardServiceDto.toDomain(): GuardService = GuardService(
    name = this.name,
    displayedName = null,
    phoneNumber = null,
    addresses = this.addresses
)