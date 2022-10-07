package kobramob.rubeg38.ru.myprotection.feature.splashscreen.data

import kobramob.rubeg38.ru.myprotection.data.SharedPreferencesHelper
import ru.rubeg38.protocolclient.Address

class AddressesRepositoryImpl(
    private val addressesApi: AddressesApi,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : AddressesRepository {

    override suspend fun getAddresses(cityName: String, guardServiceName: String): List<String> {
        try {
            val hosts = addressesApi.getAddresses(cityName, guardServiceName)
            val ipAddresses = resolveDomainNames(hosts)

            val guardService = sharedPreferencesHelper.guardService
            sharedPreferencesHelper.guardService = guardService?.copy(addresses = ipAddresses)
        } catch (_: Exception) { }

        return sharedPreferencesHelper.guardService?.addresses ?: emptyList()
    }

    private suspend fun resolveDomainNames(hosts: List<String>) =
        Address.createAll(hosts, 1).map { address -> address.ip }

}