package kobramob.rubeg38.ru.myprotection.feature.events.data

import kobramob.rubeg38.ru.myprotection.feature.events.data.models.EventEntity
import kobramob.rubeg38.ru.myprotection.feature.events.domain.models.Event
import kobramob.rubeg38.ru.myprotection.feature.events.domain.toDomain
import kobramob.rubeg38.ru.myprotection.feature.events.domain.toEntity

class EventsRepositoryImpl(
    private val eventsApi: EventsApi,
    private val eventDao: EventDao
) : EventsRepository {

    override suspend fun getEvents(facilityId: String): List<Event> {
        val events = eventsApi.getEvents(facilityId).events.map { eventDto ->
            eventDto.toDomain(facilityId)
        }

        val minId = events.minOf { event -> event.id }
        val maxId = events.maxOf { event -> event.id }

        eventDao.insertAll(events.map(Event::toEntity))

        return eventDao.getAllInRange(facilityId, minId, maxId).map(EventEntity::toDomain)
    }

    override suspend fun getEvents(facilityId: String, range: IntRange): List<Event> {
        val cachedEvents = eventDao.getAllInRange(facilityId, range.first, range.last).map(EventEntity::toDomain)

        if (cachedEvents.count() == range.last - range.first + 1) {
            return cachedEvents
        }

        val events = eventsApi.getEvents(facilityId, range.first).events.map { eventDto ->
            eventDto.toDomain(facilityId)
        }

        eventDao.insertAll(events.map(Event::toEntity))

        return eventDao.getAllInRange(facilityId, range.first, range.last).map(EventEntity::toDomain)
    }

}