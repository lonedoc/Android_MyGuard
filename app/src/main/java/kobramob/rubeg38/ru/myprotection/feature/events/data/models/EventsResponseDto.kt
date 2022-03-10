package kobramob.rubeg38.ru.myprotection.feature.events.data.models

import com.google.gson.annotations.SerializedName

data class EventsResponseDto(
    @SerializedName("com")
    val command: String,
    @SerializedName("data")
    val events: List<EventDto>
)