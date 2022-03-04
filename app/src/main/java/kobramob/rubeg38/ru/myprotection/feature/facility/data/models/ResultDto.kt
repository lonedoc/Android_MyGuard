package kobramob.rubeg38.ru.myprotection.feature.facility.data.models

import com.google.gson.annotations.SerializedName

data class ResultDto(
    @SerializedName("\$c$")
    val client: String,
    @SerializedName("com")
    val command: String,
    @SerializedName("result")
    val result: String
)