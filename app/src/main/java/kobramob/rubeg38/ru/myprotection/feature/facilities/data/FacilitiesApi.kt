package kobramob.rubeg38.ru.myprotection.feature.facilities.data

import android.util.Log
import com.google.gson.Gson
import kobramob.rubeg38.ru.myprotection.BuildConfig
import kobramob.rubeg38.ru.myprotection.data.models.FacilityDto
import kobramob.rubeg38.ru.myprotection.feature.facilities.data.models.FacilitiesResponse
import kobramob.rubeg38.ru.myprotection.utils.SessionDataHolder
import ru.rubeg38.protocolclient.Address
import ru.rubeg38.protocolclient.Client
import ru.rubeg38.protocolclient.clientConfig
import ru.rubeg38.protocolclient.retry

class FacilitiesApi(private val sessionDataHolder: SessionDataHolder) {

    suspend fun getFacilities(): List<FacilityDto> {
        val addresses = sessionDataHolder.getAddresses()
        val token = sessionDataHolder.getToken()
        val query = "{\"\$c$\":\"newlk\",\"com\":\"getobject\"}"

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

        val response = Gson().fromJson(json, FacilitiesResponse::class.java)

        if (response.result != "ok") {
            throw Exception("Server error")
        }

        return response.facilities
    }

}