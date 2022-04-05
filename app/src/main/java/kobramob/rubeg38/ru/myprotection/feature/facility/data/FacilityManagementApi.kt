package kobramob.rubeg38.ru.myprotection.feature.facility.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import kobramob.rubeg38.ru.myprotection.BuildConfig
import kobramob.rubeg38.ru.myprotection.data.models.FacilityDto
import kobramob.rubeg38.ru.myprotection.feature.facility.data.models.ResultDto
import kobramob.rubeg38.ru.myprotection.feature.facility.data.models.FacilityResponse
import kobramob.rubeg38.ru.myprotection.feature.facility.data.models.RenamingResultDto
import kobramob.rubeg38.ru.myprotection.utils.SessionDataHolder
import ru.rubeg38.protocolclient.Address
import ru.rubeg38.protocolclient.Client
import ru.rubeg38.protocolclient.clientConfig
import ru.rubeg38.protocolclient.retry

class FacilityManagementApi(private val sessionDataHolder: SessionDataHolder) {

    suspend fun getFacility(facilityId: String): FacilityDto {
        val addresses = sessionDataHolder.getAddresses()
        val token = sessionDataHolder.getToken()
        val query = getFacilityQuery(facilityId)

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

        val response = Gson().fromJson(json, FacilityResponse::class.java)

        if (response.facility == null) {
            throw Exception("Server error")
        }

        return response.facility
    }

    suspend fun setName(facilityId: String, name: String): RenamingResultDto {
        val addresses = sessionDataHolder.getAddresses()
        val token = sessionDataHolder.getToken()
        val query = getRenameQuery(facilityId, name)

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

        return Gson().fromJson(json, RenamingResultDto::class.java)
    }

    suspend fun startAlarm(facilityId: String): ResultDto {
        val addresses = sessionDataHolder.getAddresses()
        val token = sessionDataHolder.getToken()
        val query = getAlarmQuery(facilityId)

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

    suspend fun cancelAlarm(facilityId: String, passcode: String): ResultDto {
        val addresses = sessionDataHolder.getAddresses()
        val token = sessionDataHolder.getToken()
        val query = getCancelAlarmQuery(facilityId, passcode)

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

    suspend fun arm(facilityId: String): ResultDto =
        changeStatus(facilityId, "1")

    suspend fun armPerimeter(facilityId: String): ResultDto =
        changeStatus(facilityId, "2")


    suspend fun disarm(facilityId: String): ResultDto =
        changeStatus(facilityId, "0")


    private suspend fun changeStatus(facilityId: String, status: String): ResultDto {
        val addresses = sessionDataHolder.getAddresses()
        val token = sessionDataHolder.getToken()
        val query = getStatusChangeQuery(facilityId, status)

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

    private fun getFacilityQuery(facilityId: String) = JsonObject().run {
        addProperty("\$c$", "newlk")
        addProperty("com", "objectdata")
        addProperty("n_abs", facilityId)
        toString()
    }

    private fun getRenameQuery(facilityId: String, name: String) = JsonObject().run {
        addProperty("\$c$", "newlk")
        addProperty("com", "lkpspput")
        addProperty("obj", facilityId)
        addProperty("newdesc", name)
        toString()
    }

    private fun getAlarmQuery(facilityId: String) = JsonObject().run {
        addProperty("\$c$", "newlk")
        addProperty("com", "alarmtk")
        addProperty("obj", facilityId)
        toString()
    }

    private fun getCancelAlarmQuery(facilityId: String, passcode: String) = JsonObject().run {
        addProperty("\$c$", "newlk")
        addProperty("com", "cancelalarm")
        addProperty("obj", facilityId)
        addProperty("passcode", passcode)
        toString()
    }

    private fun getStatusChangeQuery(facilityId: String, status: String) = JsonObject().run {
        addProperty("\$c$", "newlk")
        addProperty("com", "guard")
        addProperty("obj", facilityId)
        addProperty("status", status)
        toString()
    }

}