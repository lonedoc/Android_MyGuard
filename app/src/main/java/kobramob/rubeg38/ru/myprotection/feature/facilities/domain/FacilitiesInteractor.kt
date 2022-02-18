package kobramob.rubeg38.ru.myprotection.feature.facilities.domain

import kobramob.rubeg38.ru.myprotection.feature.facilities.data.FacilitiesRepository
import kobramob.rubeg38.ru.myprotection.utils.attempt

class FacilitiesInteractor(private val facilitiesRepository: FacilitiesRepository) {

    suspend fun getFacilities() = attempt { facilitiesRepository.getFacilities() }

}