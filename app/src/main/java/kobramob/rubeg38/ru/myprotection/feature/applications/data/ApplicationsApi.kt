package kobramob.rubeg38.ru.myprotection.feature.applications.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import kobramob.rubeg38.ru.myprotection.BuildConfig
import kobramob.rubeg38.ru.myprotection.feature.applications.data.models.ApplicationResult
import kobramob.rubeg38.ru.myprotection.feature.applications.data.models.PredefinedApplication
import kobramob.rubeg38.ru.myprotection.feature.applications.data.models.PredefinedApplicationsResponse
import kobramob.rubeg38.ru.myprotection.utils.SessionDataHolder
import ru.rubeg38.protocolclient.Client
import ru.rubeg38.protocolclient.retry
import java.util.*

class ApplicationsApi(private val sessionDataHolder: SessionDataHolder) {

    private val predefinedApplicationsQuery: String by lazy {
        JsonObject().run {
            addProperty("\$c$", "newlk")
            addProperty("com", "get.catalog")
            addProperty("name", "zaytext.sp8")
            toString()
        }
    }

    suspend fun getPredefinedApplications(): List<PredefinedApplication> {
        val addresses = sessionDataHolder.getAddresses()
        val token = sessionDataHolder.getToken()
        val query = predefinedApplicationsQuery

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

        val response = Gson().fromJson(json, PredefinedApplicationsResponse::class.java)

        if (response.result != "ok") {
            throw Exception("Server error")
        }

        return response.applications
    }

    suspend fun sendApplication(facilityId: String, text: String, timestamp: Date): ApplicationResult {
        val addresses = sessionDataHolder.getAddresses()
        val token = sessionDataHolder.getToken()
        val query = getSendApplicationQuery(facilityId, text, timestamp)

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

        return Gson().fromJson(json, ApplicationResult::class.java)
    }

    private fun getSendApplicationQuery(facilityId: String, text: String, timestamp: Date) =
        JsonObject().run {
            addProperty("\$c$", "newlk")
            addProperty("com", "application")
            addProperty("obj", facilityId)
            addProperty("text", text)
            addProperty("datetime", timestamp.time / 1000)
            toString()
        }

}