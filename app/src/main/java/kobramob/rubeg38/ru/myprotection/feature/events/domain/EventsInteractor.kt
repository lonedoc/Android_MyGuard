package kobramob.rubeg38.ru.myprotection.feature.events.domain

import kobramob.rubeg38.ru.myprotection.feature.events.data.EventsRepository
import kobramob.rubeg38.ru.myprotection.utils.attempt

class EventsInteractor(private val eventsRepository: EventsRepository) {

    suspend fun getEvents(facilityId: String, range: IntRange? = null) = attempt {
        if (range == null) {
            eventsRepository.getEvents(facilityId)
        } else {
            eventsRepository.getEvents(facilityId, range)
        }
    }

}