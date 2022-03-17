package kobramob.rubeg38.ru.myprotection.feature.testmode.data.models

import com.google.gson.annotations.SerializedName

data class TestingStatusDto(
    @SerializedName("\$c$")
    val client: String,
    @SerializedName("com")
    val command: String,
    @SerializedName("result")
    val result: String,
    @SerializedName("s")
    val isAlarmButtonPressed: Int,
    @SerializedName("p")
    val isAlarmButtonReinstalled: Int
)