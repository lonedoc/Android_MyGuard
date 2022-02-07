package kobramob.rubeg38.ru.myprotection.feature.login.data

import kobramob.rubeg38.ru.myprotection.feature.login.domain.models.City
import kobramob.rubeg38.ru.myprotection.feature.login.domain.toDomain

class GuardServicesRepositoryImpl(
    private val guardServicesApi: GuardServicesApi
) : GuardServicesRepository {

    override suspend fun getCities(): List<City> =
        guardServicesApi.getCitiesData().citiesData.toDomain()

}