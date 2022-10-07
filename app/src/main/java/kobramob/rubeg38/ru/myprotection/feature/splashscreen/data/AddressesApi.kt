package kobramob.rubeg38.ru.myprotection.feature.splashscreen.data

import android.util.Log
import com.google.gson.JsonParser
import kobramob.rubeg38.ru.myprotection.BuildConfig
import ru.rubeg38.protocolclient.Address
import ru.rubeg38.protocolclient.Client
import ru.rubeg38.protocolclient.clientConfig
import ru.rubeg38.protocolclient.retry
import java.lang.Exception

private const val HOSTNAME = "lk.rubeg38.ru"
private const val PORT = 8300

class AddressesApi {

    suspend fun getAddresses(cityName: String, guardServiceName: String): List<String> {
        val addresses = Address.createAll(HOSTNAME, PORT)
        val query = "{\"\$c$\":\"getip\", \"city\":\"$cityName\", \"pr\":\"$guardServiceName\"}"

        if (BuildConfig.DEBUG) {
            Log.d(this::class.simpleName, "-> $query")
        }

        val data = retry(-1, addresses) { address ->
            val config = clientConfig {
                setSkipUnidentified(false)
            }

            val client = Client(config)
            client.bind(address)
            client.sendRequest(query, null)
        }

        val json = String(data)

        if (BuildConfig.DEBUG) {
            Log.d(this::class.simpleName, "<- $json")
        }

        val jsonObject = JsonParser.parseString(json).asJsonObject
        val command = jsonObject.get("\$c$")?.asString

        val payload = jsonObject.get("data")?.asJsonArray?.map { jsonElement ->
            jsonElement.asString
        }

        if (command != "getip" || payload == null) {
            throw Exception("Parse error")
        }

        return payload
    }

}