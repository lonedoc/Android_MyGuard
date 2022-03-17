package kobramob.rubeg38.ru.myprotection.feature.testmode.data.models

import com.google.gson.annotations.SerializedName

data class StartTestingResponseDto(
    @SerializedName("\$c$")
    val client: String,
    @SerializedName("com")
    val command: String,
    @SerializedName("result")
    val result: String,
    @SerializedName("time")
    val timeLeft: String
)