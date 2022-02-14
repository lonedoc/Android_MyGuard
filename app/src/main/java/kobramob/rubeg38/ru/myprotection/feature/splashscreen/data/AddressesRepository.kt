package kobramob.rubeg38.ru.myprotection.feature.splashscreen.data

interface AddressesRepository {
    suspend fun getAddresses(cityName: String, guardServiceName: String): List<String>
}