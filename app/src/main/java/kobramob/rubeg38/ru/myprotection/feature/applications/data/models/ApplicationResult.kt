package kobramob.rubeg38.ru.myprotection.feature.applications.data.models

import com.google.gson.annotations.SerializedName

data class ApplicationResult(
    @SerializedName("\$c$")
    val client: String,
    @SerializedName("com")
    val command: String,
    @SerializedName("result")
    val result: String
)