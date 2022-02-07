package kobramob.rubeg38.ru.myprotection.feature.login.domain

import kobramob.rubeg38.ru.myprotection.data.SharedPreferencesHelper
import kobramob.rubeg38.ru.myprotection.domain.models.GuardService
import kobramob.rubeg38.ru.myprotection.feature.login.data.GuardServicesRepository
import kobramob.rubeg38.ru.myprotection.utils.attempt

class LoginFormInteractor(
    private val repository: GuardServicesRepository,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) {

    suspend fun getCities() = attempt { repository.getCities() }

    fun saveCityName(cityName: String) {
        sharedPreferencesHelper.cityName = cityName
    }

    fun saveGuardService(guardService: GuardService) {
        sharedPreferencesHelper.guardService = guardService
    }

    fun savePhoneNumber(phoneNumber: String) {
        sharedPreferencesHelper.userPhoneNumber = phoneNumber
    }

    fun getCachedCityName(): String? = sharedPreferencesHelper.cityName

    fun getCachedGuardService(): GuardService? = sharedPreferencesHelper.guardService

    fun getCachedPhoneNumber(): String? = sharedPreferencesHelper.userPhoneNumber

}
