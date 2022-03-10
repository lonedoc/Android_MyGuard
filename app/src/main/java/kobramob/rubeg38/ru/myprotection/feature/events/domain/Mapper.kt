package kobramob.rubeg38.ru.myprotection.feature.events.domain

import android.annotation.SuppressLint
import kobramob.rubeg38.ru.myprotection.feature.events.data.models.EventDto
import kobramob.rubeg38.ru.myprotection.feature.events.data.models.EventEntity
import kobramob.rubeg38.ru.myprotection.feature.events.domain.models.Event
import java.text.SimpleDateFormat

fun EventDto.toDomain(facilityId: String) = Event(
    facilityId = facilityId,
    id = id,
    timestamp = parseDate(timestamp),
    type = type,
    description = description,
    zone = zone
)

@SuppressLint("SimpleDateFormat")
private fun parseDate(dateStr: String) =
    SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(dateStr)!!

fun EventEntity.toDomain() = Event(
    facilityId = facilityId,
    id = id,
    timestamp = timestamp,
    type = type,
    description = description,
    zone = zone
)

fun Event.toEntity() = EventEntity(
    facilityId = facilityId,
    id = id,
    timestamp = timestamp,
    type = type,
    description = description,
    zone = zone
)