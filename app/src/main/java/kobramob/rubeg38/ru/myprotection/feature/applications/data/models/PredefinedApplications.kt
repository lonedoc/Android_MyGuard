package kobramob.rubeg38.ru.myprotection.feature.applications.data.models

import com.google.gson.annotations.SerializedName

class PredefinedApplicationsResponse( // TODO: Rename
    @SerializedName("\$c$")
    val client: String,
    @SerializedName("com")
    val command: String,
    @SerializedName("result")
    val result: String,
    @SerializedName("data")
    val applications: List<PredefinedApplication>
)