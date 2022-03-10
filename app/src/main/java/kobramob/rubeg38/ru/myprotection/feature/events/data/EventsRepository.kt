package kobramob.rubeg38.ru.myprotection.feature.events.data

import kobramob.rubeg38.ru.myprotection.feature.events.domain.models.Event

interface EventsRepository {
    suspend fun getEvents(facilityId: String): List<Event>
    suspend fun getEvents(facilityId: String, range: IntRange): List<Event>
}