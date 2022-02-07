package kobramob.rubeg38.ru.myprotection.feature.login.data

import kobramob.rubeg38.ru.myprotection.feature.login.domain.models.City

interface GuardServicesRepository {
    suspend fun getCities(): List<City>
}