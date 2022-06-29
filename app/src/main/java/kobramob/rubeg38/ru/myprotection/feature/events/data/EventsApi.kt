package kobramob.rubeg38.ru.myprotection.feature.events.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import kobramob.rubeg38.ru.myprotection.BuildConfig
import kobramob.rubeg38.ru.myprotection.feature.events.data.models.EventsResponseDto
import kobramob.rubeg38.ru.myprotection.utils.SessionDataHolder
import ru.rubeg38.protocolclient.Client
import ru.rubeg38.protocolclient.retry

private const val RETRIES_COUNT = 3

class EventsApi(private val sessionDataHolder: SessionDataHolder) {

    suspend fun getEvents(facilityId: String, position: Int? = null): EventsResponseDto {
        val addresses = sessionDataHolder.getAddresses()
        val token = sessionDataHolder.getToken()
        val query = getEventsQuery(facilityId, position)

        if (BuildConfig.DEBUG) {
            Log.d(this::class.simpleName, "-> $query")
        }

        val data = retry(RETRIES_COUNT, addresses) { address ->
            val client = Client()
            client.bind(address)
            client.sendRequest(query, token)
        }

        val json = String(data)

        if (BuildConfig.DEBUG) {
            Log.d(this::class.simpleName, "<- $json")
        }

        return Gson().fromJson(json, EventsResponseDto::class.java)
    }

    private fun getEventsQuery(facilityId: String, position: Int?) = JsonObject().run {
        addProperty("\$c$", "newlk")
        addProperty("com", "getevents")
        addProperty("obj", facilityId)

        if (position != null) {
            addProperty("pos", position)
        }

        toString()
    }

}