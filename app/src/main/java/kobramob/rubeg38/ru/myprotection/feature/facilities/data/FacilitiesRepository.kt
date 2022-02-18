package kobramob.rubeg38.ru.myprotection.feature.facilities.data

import kobramob.rubeg38.ru.myprotection.domain.models.Facility

interface FacilitiesRepository {
    suspend fun getFacilities(): List<Facility>
}