package kobramob.rubeg38.ru.myprotection.feature.facilities.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import kobramob.rubeg38.ru.myprotection.BuildConfig
import kobramob.rubeg38.ru.myprotection.data.models.FacilityDto
import kobramob.rubeg38.ru.myprotection.feature.facilities.data.models.FacilitiesResponse
import kobramob.rubeg38.ru.myprotection.utils.SessionDataHolder
import ru.rubeg38.protocolclient.Client
import ru.rubeg38.protocolclient.retry

class FacilitiesApi(private val sessionDataHolder: SessionDataHolder) {

    private val facilitiesQuery by lazy {
        JsonObject().run {
            addProperty("\$c$", "newlk")
            addProperty("com", "getobject")
            toString()
        }
    }

    suspend fun getFacilities(): List<FacilityDto> {
        val addresses = sessionDataHolder.getAddresses()
        val token = sessionDataHolder.getToken()

        if (BuildConfig.DEBUG) {
            Log.d(this::class.simpleName, "-> $facilitiesQuery")
        }

        val data = retry(3, addresses) { address ->
            val client = Client()
            client.bind(address)
            client.sendRequest(facilitiesQuery, token)
        }

        val json = String(data)

        if (BuildConfig.DEBUG) {
            Log.d(this::class.simpleName, "<- $json")
        }

        val response = Gson().fromJson(json, FacilitiesResponse::class.java)

        if (response.result != "ok") {
            throw Exception("Server error")
        }

        return response.facilities
    }

}