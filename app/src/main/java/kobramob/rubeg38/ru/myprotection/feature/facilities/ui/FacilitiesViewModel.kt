package kobramob.rubeg38.ru.myprotection.feature.facilities.ui

import android.net.Uri
import androidx.lifecycle.*
import com.github.terrakok.cicerone.Router
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.Screens
import kobramob.rubeg38.ru.myprotection.base.BaseViewModel
import kobramob.rubeg38.ru.myprotection.base.Event
import kobramob.rubeg38.ru.myprotection.feature.facilities.domain.FacilitiesInteractor
import kobramob.rubeg38.ru.myprotection.feature.facilities.domain.models.Sorting
import kobramob.rubeg38.ru.myprotection.utils.SingleLiveEvent
import kobramob.rubeg38.ru.myprotection.utils.schedule
import kotlinx.coroutines.launch
import java.util.*

private const val UPDATE_INTERVAL: Long = 10_000

class FacilitiesViewModel(
    private val interactor: FacilitiesInteractor,
    private val router: Router
) : BaseViewModel<ViewState>(), LifecycleEventObserver {

    private var timer: Timer? = null

    val singleEvent = SingleLiveEvent<SingleEvent>()

    init {
        processDataEvent(DataEvent.OnFacilitiesRequest)
    }

    override fun initialViewState() = ViewState(
        isPlaceholderShown = true,
        isRefresherShown = false,
        isUserInitiatedRequestExecuting = false,
        facilities = emptyList(),
        sorting = Sorting.BY_STATUS,
        comparator = FacilityComparator.StatusFirstComparator
    )

    override suspend fun reduce(event: Event, previousState: ViewState): ViewState? {
        when(event) {
            is UiEvent.OnCallButtonClick -> {
                interactor.getGuardServicePhoneNumber()?.let { phoneNumber ->
                    val uri = Uri.parse("tel:$phoneNumber")
                    singleEvent.postValue(SingleEvent.OnCall(uri))
                    return null
                }

                singleEvent.postValue(SingleEvent.OnError(R.string.phone_number_not_found_message))
                return null
            }
            is UiEvent.OnSortButtonClick -> {
                singleEvent.postValue(SingleEvent.OnSortingDialog(previousState.sorting))
                return null
            }
            is UiEvent.OnSortingChanged -> {
                val comparator = when (event.sorting) {
                    Sorting.BY_STATUS -> FacilityComparator.StatusFirstComparator
                    Sorting.BY_NAME_ASCENDING -> FacilityComparator.NameFirstAscendingComparator
                    Sorting.BY_NAME_DESCENDING -> FacilityComparator.NameFirstDescendingComparator
                    Sorting.BY_ADDRESS_ASCENDING -> FacilityComparator.AddressFirstAscendingComparator
                    Sorting.BY_ADDRESS_DESCENDING -> FacilityComparator.AddressFirstDescendingComparator
                }

                return previousState.copy(
                    sorting = event.sorting,
                    comparator = comparator,
                    facilities = previousState.facilities.sortedWith(comparator)
                )
            }
            is UiEvent.OnFacilitiesRequest -> {
                processDataEvent(DataEvent.OnFacilitiesRequest)
                return previousState.copy(
                    isRefresherShown = true,
                    isUserInitiatedRequestExecuting = true
                )
            }
            is UiEvent.OnFacilityItemClick -> {
                router.navigateTo(Screens.facility(event.facility))
                return null
            }
            is DataEvent.OnFacilitiesRequest -> {
                viewModelScope.launch {
                    interactor.getFacilities().fold(
                        onSuccess = { facilities ->
                            processDataEvent(DataEvent.OnFacilitiesUpdate(facilities))
                        },
                        onError = {
                            processDataEvent(DataEvent.OnFacilitiesRequestFail)
                        }
                    )
                }

                return null
            }
            is DataEvent.OnFacilitiesRequestFail -> {
                if (previousState.isUserInitiatedRequestExecuting) {
                    val errorMessageRes = R.string.facilities_update_failed_message
                    singleEvent.postValue(SingleEvent.OnError(errorMessageRes))
                }

                return previousState.copy(
                    isRefresherShown = false,
                    isUserInitiatedRequestExecuting = false
                )
            }
            is DataEvent.OnFacilitiesUpdate -> {
                return previousState.copy(
                    isPlaceholderShown = false,
                    isRefresherShown = false,
                    isUserInitiatedRequestExecuting = false,
                    facilities = event.facilities.sortedWith(previousState.comparator)
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
                        processDataEvent(DataEvent.OnFacilitiesRequest)
                    }
                }
            }
            Lifecycle.Event.ON_PAUSE -> {
                timer?.cancel()
            }
            else -> return
        }
    }

}