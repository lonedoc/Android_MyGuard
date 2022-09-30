package kobramob.rubeg38.ru.myprotection.feature.login.data

import com.google.gson.Gson
import kobramob.rubeg38.ru.myprotection.feature.login.data.models.CitiesResponseDto
import ru.rubeg38.protocolclient.Address
import ru.rubeg38.protocolclient.Client
import ru.rubeg38.protocolclient.clientConfig
import ru.rubeg38.protocolclient.retry

private val ipAddresses = listOf("91.189.160.38", "87.103.172.170")
private const val port = 8300

class GuardServicesApi {

    suspend fun getCitiesData(): CitiesResponseDto {
        val addresses = ipAddresses.map { ip -> Address.create(ip, port) }
        val query = "{\"\$c$\": \"getcity\"}"

        val data = retry(-1, addresses) { address ->
            val config = clientConfig {
                setSkipUnidentified(false)
            }

            val client = Client(config)
            client.bind(address)
            client.sendRequest(query, null)
        }

        val json = String(data)

        return Gson().fromJson(json, CitiesResponseDto::class.java)
    }

}