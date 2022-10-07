package kobramob.rubeg38.ru.myprotection.feature.splashscreen.domain

import kobramob.rubeg38.ru.myprotection.data.SharedPreferencesHelper
import kobramob.rubeg38.ru.myprotection.feature.splashscreen.data.AddressesRepository
import kobramob.rubeg38.ru.myprotection.utils.attempt

class SplashScreenInteractor(
    private val addressesRepository: AddressesRepository,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) {

    fun getCityName(): String? = sharedPreferencesHelper.cityName

    fun getGuardServiceName(): String? = sharedPreferencesHelper.guardService?.name

    fun getUserPhoneNumber(): String? = sharedPreferencesHelper.userPhoneNumber

    fun getToken(): String? = sharedPreferencesHelper.token

    suspend fun getAddresses(cityName: String, guardServiceName: String) =
        addressesRepository.getAddresses(cityName, guardServiceName)

}