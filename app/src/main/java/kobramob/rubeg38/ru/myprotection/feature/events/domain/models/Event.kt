package kobramob.rubeg38.ru.myprotection.feature.events.domain.models

import java.util.*

data class Event(
    val facilityId: String,
    val id: Int,
    val timestamp: Date,
    val type: Int,
    val description: String,
    val zone: String
)