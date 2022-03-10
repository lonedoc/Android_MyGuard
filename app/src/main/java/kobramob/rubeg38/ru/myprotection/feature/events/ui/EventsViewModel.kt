package kobramob.rubeg38.ru.myprotection.feature.events.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.base.BaseViewModel
import kobramob.rubeg38.ru.myprotection.base.Event
import kobramob.rubeg38.ru.myprotection.feature.events.domain.EventsInteractor
import kobramob.rubeg38.ru.myprotection.feature.events.domain.models.Event as EventModel
import kobramob.rubeg38.ru.myprotection.utils.SingleLiveEvent
import kobramob.rubeg38.ru.myprotection.utils.schedule
import kotlinx.coroutines.launch
import java.util.*

private const val PAGE_SIZE = 20
private const val UPDATE_INTERVAL: Long = 10_000

class EventsViewModel(
    private val facilityId: String,
    private val interactor: EventsInteractor
) : BaseViewModel<ViewState>(), LifecycleEventObserver {

    private var timer: Timer? = null

    val singleEvent = SingleLiveEvent<SingleEvent>()

    init {
        processDataEvent(DataEvent.OnEventsRequest(null))
    }

    override fun initialViewState() = ViewState(
        isPlaceholderShown = true,
        isRefresherShown = false,
        isUserInitiatedRequestExecuting = false,
        events = emptyList()
    )

    override suspend fun reduce(event: Event, previousState: ViewState): ViewState? {
        when(event) {
            is UiEvent.OnEventsRequest -> {
                processDataEvent(DataEvent.OnEventsRequest(null))
                return previousState.copy(
                    isRefresherShown = true,
                    isUserInitiatedRequestExecuting = true
                )
            }
            is UiEvent.OnEndOfListReached -> {
                val minEventId = previousState.events.minOf { it.id }
                var lowerBound = minEventId - PAGE_SIZE

                if (lowerBound < 0) {
                    lowerBound = 0
                }

                val upperBound = lowerBound + PAGE_SIZE
                val range = lowerBound until upperBound

                processDataEvent(DataEvent.OnEventsRequest(range))

                return null
            }
            is DataEvent.OnEventsRequest -> {
                viewModelScope.launch {
                    interactor.getEvents(facilityId, event.range).fold(
                        onSuccess = { events ->
                            processDataEvent(DataEvent.OnEventsUpdate(events))
                        },
                        onError = {
                            processDataEvent(DataEvent.OnEventsRequestFail)
                        }
                    )
                }

                return null
            }
            is DataEvent.OnEventsRequestFail -> {
                if (previousState.isUserInitiatedRequestExecuting) {
                    val errorMessageRes = R.string.events_update_failed_message
                    singleEvent.postValue(SingleEvent.OnError(errorMessageRes))
                }

                return previousState.copy(
                    isRefresherShown = false,
                    isUserInitiatedRequestExecuting = false
                )
            }
            is DataEvent.OnEventsUpdate -> {
                val events = mergeEvents(previousState.events, event.events)

                return previousState.copy(
                    isPlaceholderShown = false,
                    isRefresherShown = false,
                    isUserInitiatedRequestExecuting = false,
                    events = events
                )
            }
            else -> return null
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                timer?.cancel()
                timer = Timer().apply {
                    schedule(delay = 0, period = UPDATE_INTERVAL) {
                        processDataEvent(DataEvent.OnEventsRequest(null))
                    }
                }
            }
            Lifecycle.Event.ON_PAUSE -> {
                timer?.cancel()
            }
            else -> return
        }
    }

    private fun mergeEvents(lhs: List<EventModel>, rhs: List<EventModel>) =
        (lhs + rhs)
            .distinctBy { event -> event.id }
            .sortedByDescending { event -> event.id }

}