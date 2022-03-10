package kobramob.rubeg38.ru.myprotection.feature.events.data.models

import com.google.gson.annotations.SerializedName

data class EventDto(
    @SerializedName("ab")
    val id: Int,
    @SerializedName("ti")
    val timestamp: String,
    @SerializedName("cs")
    val type: Int,
    @SerializedName("nc")
    val description: String,
    @SerializedName("de")
    val zone: String
)