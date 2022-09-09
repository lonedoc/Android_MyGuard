package kobramob.rubeg38.ru.myprotection.feature.facilities.domain

import kobramob.rubeg38.ru.myprotection.data.SharedPreferencesHelper
import kobramob.rubeg38.ru.myprotection.feature.facilities.data.FacilitiesRepository
import kobramob.rubeg38.ru.myprotection.utils.attempt

class FacilitiesInteractor(
    private val facilitiesRepository: FacilitiesRepository,
    private val preferences: SharedPreferencesHelper
) {

    suspend fun getFacilities() = attempt { facilitiesRepository.getFacilities() }

    suspend fun getGuardServicePhoneNumber() = preferences.guardService?.phoneNumber

}