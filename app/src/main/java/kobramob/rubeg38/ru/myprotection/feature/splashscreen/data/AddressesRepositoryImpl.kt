package kobramob.rubeg38.ru.myprotection.feature.splashscreen.data

class AddressesRepositoryImpl(private val addressesApi: AddressesApi) : AddressesRepository {

    override suspend fun getAddresses(cityName: String, guardServiceName: String): List<String> =
        addressesApi.getAddresses(cityName, guardServiceName)

}