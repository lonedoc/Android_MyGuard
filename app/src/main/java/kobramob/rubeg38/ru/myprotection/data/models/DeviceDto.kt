package kobramob.rubeg38.ru.myprotection.data.models

import com.google.gson.annotations.SerializedName

data class DeviceDto(
    @SerializedName("type")
    val type: String,
    @SerializedName("number")
    val number: Int,
    @SerializedName("desc")
    val deviceDescription: String,
    @SerializedName("online")
    val isOnline: Int?,
    @SerializedName("value")
    val value: Double?
)