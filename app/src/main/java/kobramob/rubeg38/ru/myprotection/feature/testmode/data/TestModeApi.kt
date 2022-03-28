package kobramob.rubeg38.ru.myprotection.feature.testmode.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import kobramob.rubeg38.ru.myprotection.BuildConfig
import kobramob.rubeg38.ru.myprotection.feature.testmode.data.models.ResultDto
import kobramob.rubeg38.ru.myprotection.feature.testmode.data.models.StartTestingResponseDto
import kobramob.rubeg38.ru.myprotection.feature.testmode.data.models.TestingStatusDto
import kobramob.rubeg38.ru.myprotection.utils.SessionDataHolder
import ru.rubeg38.protocolclient.Address
import ru.rubeg38.protocolclient.Client
import ru.rubeg38.protocolclient.retry

class TestModeApi(private val sessionDataHolder: SessionDataHolder) {

    suspend fun startTesting(facilityId: String): StartTestingResponseDto {
        val addresses = sessionDataHolder.getAddresses()
        val token = sessionDataHolder.getToken()
        val query = getStartTestingQuery(facilityId)

        if (BuildConfig.DEBUG) {
            Log.d(this::class.simpleName, "-> $query")
        }

        val data = retry(3, addresses) { address ->
            val client = Client()
            client.bind(address)
            client.sendRequest(query, token)
        }

        val json = String(data)

        if (BuildConfig.DEBUG) {
            Log.d(this::class.simpleName, "<- $json")
        }

        return Gson().fromJson(json, StartTestingResponseDto::class.java)
    }

    suspend fun endTesting(facilityId: String): ResultDto {
        val addresses = sessionDataHolder.getAddresses()
        val token = sessionDataHolder.getToken()
        val query = getEndTestingQuery(facilityId)

        if (BuildConfig.DEBUG) {
            Log.d(this::class.simpleName, "-> $query")
        }

        val data = retry(3, addresses) { address ->
            val client = Client()
            client.bind(address)
            client.sendRequest(query, token)
        }

        val json = String(data)

        if (BuildConfig.DEBUG) {
            Log.d(this::class.simpleName, "<- $json")
        }

        return Gson().fromJson(json, ResultDto::class.java)
    }

    suspend fun reset(facilityId: String): ResultDto {
        val addresses = sessionDataHolder.getAddresses()
        val token = sessionDataHolder.getToken()
        val query = getResetQuery(facilityId)

        if (BuildConfig.DEBUG) {
            Log.d(this::class.simpleName, "-> $query")
        }

        val data = retry(3, addresses) { address ->
            val client = Client()
            client.bind(address)
            client.sendRequest(query, token)
        }

        val json = String(data)

        if (BuildConfig.DEBUG) {
            Log.d(this::class.simpleName, "<- $json")
        }

        return Gson().fromJson(json, ResultDto::class.java)
    }

    suspend fun getStatus(facilityId: String): TestingStatusDto {
        val addresses = sessionDataHolder.getAddresses()
        val token = sessionDataHolder.getToken()
        val query = getStatusQuery(facilityId)

        if (BuildConfig.DEBUG) {
            Log.d(this::class.simpleName, "-> $query")
        }

        val data = retry(3, addresses) { address ->
            val client = Client()
            client.bind(address)
            client.sendRequest(query, token)
        }

        val json = String(data)

        if (BuildConfig.DEBUG) {
            Log.d(this::class.simpleName, "<- $json")
        }

        return Gson().fromJson(json, TestingStatusDto::class.java)
    }

    private fun getStartTestingQuery(facilityId: String) = JsonObject().run {
        addProperty("\$c$", "newlk")
        addProperty("com", "checktk")
        addProperty("obj", facilityId)
        toString()
    }

    private fun getEndTestingQuery(facilityId: String) = JsonObject().run {
        addProperty("\$c$", "newlk")
        addProperty("com", "checktkend")
        addProperty("obj", facilityId)
        toString()
    }

    private fun getResetQuery(facilityId: String) = JsonObject().run {
        addProperty("\$c$", "newlk")
        addProperty("com", "checktkreset")
        addProperty("obj", facilityId)
        toString()
    }

    private fun getStatusQuery(facilityId: String) = JsonObject().run {
        addProperty("\$c$", "newlk")
        addProperty("com", "checktkstatus")
        addProperty("obj", facilityId)
        toString()
    }

}