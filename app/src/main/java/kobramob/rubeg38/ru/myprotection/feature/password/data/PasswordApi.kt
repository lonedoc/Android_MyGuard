package kobramob.rubeg38.ru.myprotection.feature.password.data

import android.util.Log
import com.google.gson.Gson
import kobramob.rubeg38.ru.myprotection.BuildConfig
import kobramob.rubeg38.ru.myprotection.feature.password.data.models.CommandResultDto
import kobramob.rubeg38.ru.myprotection.utils.SessionDataHolder
import ru.rubeg38.protocolclient.Address
import ru.rubeg38.protocolclient.Client
import ru.rubeg38.protocolclient.clientConfig
import ru.rubeg38.protocolclient.retry

private const val PORT = 8301
private const val RESULT_OK = "sendpassword"

class PasswordApi(private val sessionDataHolder: SessionDataHolder) {

    suspend fun requestPassword(phoneNumber: String): Boolean {
        val addresses = sessionDataHolder.getIpAddresses().mapNotNull { ip ->
            try {
                Address.create(ip, PORT)
            } catch (ex: Exception) {
                null
            }
        }

        require(addresses.count() > 0)

        val query = "{\"\$c$\":\"getpasswordlk\",\"phone\":\"$phoneNumber\"}"

        if (BuildConfig.DEBUG) {
            Log.d(this::class.simpleName, "-> $query")
        }

        val data = retry(3, addresses) { address ->
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

        return Gson().fromJson(json, CommandResultDto::class.java).result == RESULT_OK
    }

}