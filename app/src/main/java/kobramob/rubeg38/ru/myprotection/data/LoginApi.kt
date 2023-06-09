package kobramob.rubeg38.ru.myprotection.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kobramob.rubeg38.ru.myprotection.BuildConfig
import kobramob.rubeg38.ru.myprotection.data.models.LoginResponseDto
import kobramob.rubeg38.ru.myprotection.domain.models.Credentials
import kobramob.rubeg38.ru.myprotection.utils.SessionDataHolder
import ru.rubeg38.protocolclient.Client
import ru.rubeg38.protocolclient.clientConfig
import ru.rubeg38.protocolclient.retry

private const val LOGIN_ERROR = "regerror"
private const val LOGIN_OK = "reglkok"

class LoginApi(private val sessionDataHolder: SessionDataHolder) {

    private val logoutQuery by lazy {
        JsonObject().run {
            addProperty("\$c$", "newlk")
            addProperty("com", "deleteuser")
            toString()
        }
    }

    suspend fun login(
        credentials: Credentials,
        fcmToken: String,
        deviceName: String
    ): LoginResponseDto {
        val addresses = sessionDataHolder.getAddresses()
        val query = getLoginQuery(credentials, fcmToken, deviceName)

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

        when (parseCommand(json)) {
            LOGIN_OK -> {
                return Gson().fromJson(json, LoginResponseDto::class.java)
            }
            LOGIN_ERROR -> {
                throw Exception("Not logged in") // TODO: Create more clear error message
            }
            else -> {
                throw Exception("Parse error")
            }
        }
    }

    suspend fun logout(): Boolean {
        val addresses = sessionDataHolder.getAddresses()
        val token = sessionDataHolder.getToken()

        if (BuildConfig.DEBUG) {
            Log.d(this::class.simpleName, "-> $logoutQuery")
        }

        val data = retry(3, addresses) { address ->
            val config = clientConfig {
                setSkipUnidentified(false)
            }

            val client = Client(config)
            client.bind(address)
            client.sendRequest(logoutQuery, token)
        }

        val json = String(data)

        if (BuildConfig.DEBUG) {
            Log.d(this::class.simpleName, "<- $json")
        }

        val jsonObject = JsonParser.parseString(json).asJsonObject
        val clientType = jsonObject.get("\$c$")?.asString
        val command = jsonObject.get("com")?.asString
        val result = jsonObject.get("result")?.asString

        if (clientType != "newlk" || command != "deleteuser" || result == null) {
            throw Exception("Parse error")
        }

        return result == "ok"
    }

    private fun getLoginQuery(credentials: Credentials, fcmToken: String, deviceName: String): String {
        val jsonObject = JsonObject()
        jsonObject.addProperty("\$c$", "reglk")
        jsonObject.addProperty("id", "FCC2C586-A78D-48F7-AC3A-FC85F0AE29EF")
        jsonObject.addProperty("phone", credentials.phoneNumber)
        jsonObject.addProperty("password", credentials.password)
        jsonObject.addProperty("idgoogle", fcmToken)
        jsonObject.addProperty("os", "android")
        jsonObject.addProperty("name", deviceName)
        return jsonObject.toString()
    }

    private fun parseCommand(json: String): String? {
        val jsonObject = JsonParser.parseString(json).asJsonObject
        return jsonObject.get("\$c$")?.asString
    }

}
