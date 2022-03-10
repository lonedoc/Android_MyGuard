package kobramob.rubeg38.ru.myprotection.feature.events.ui

import androidx.annotation.StringRes
import kobramob.rubeg38.ru.myprotection.base.Event
import kobramob.rubeg38.ru.myprotection.feature.events.domain.models.Event as EventModel

data class ViewState(
    val isPlaceholderShown: Boolean,
    val isRefresherShown: Boolean,
    val isUserInitiatedRequestExecuting: Boolean,
    val events: List<EventModel>
)

sealed class UiEvent : Event {
    object OnEventsRequest : UiEvent()
    object OnEndOfListReached : UiEvent()
}

sealed class DataEvent : Event {
    object OnEventsRequestFail : DataEvent()
    data class OnEventsRequest(val range: IntRange?) : DataEvent()
    data class OnEventsUpdate(val events: List<EventModel>) : DataEvent()
}

sealed class SingleEvent {
    data class OnError(@StringRes val errorMessageRes: Int) : SingleEvent()
}