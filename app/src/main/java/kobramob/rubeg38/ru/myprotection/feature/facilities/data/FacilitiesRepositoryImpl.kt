package kobramob.rubeg38.ru.myprotection.feature.facilities.data

import kobramob.rubeg38.ru.myprotection.data.models.FacilityDto
import kobramob.rubeg38.ru.myprotection.domain.models.Facility
import kobramob.rubeg38.ru.myprotection.domain.toDomain

class FacilitiesRepositoryImpl(
    private val facilitiesApi: FacilitiesApi
) : FacilitiesRepository {

    override suspend fun getFacilities(): List<Facility> =
        facilitiesApi.getFacilities().map(FacilityDto::toDomain)

}