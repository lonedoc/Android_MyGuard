package kobramob.rubeg38.ru.myprotection.feature.facility.ui

import android.app.Application
import android.os.SystemClock.elapsedRealtime
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.Screens
import kobramob.rubeg38.ru.myprotection.base.BaseViewModel
import kobramob.rubeg38.ru.myprotection.base.Event
import kobramob.rubeg38.ru.myprotection.domain.models.Facility
import kobramob.rubeg38.ru.myprotection.domain.models.StatusCode
import kobramob.rubeg38.ru.myprotection.feature.facility.domain.FacilityInteractor
import kobramob.rubeg38.ru.myprotection.utils.SingleLiveEvent
import kobramob.rubeg38.ru.myprotection.utils.schedule
import kotlinx.coroutines.launch
import java.util.*
import kotlin.random.Random

private const val LONG_UPDATE_INTERVAL: Long = 10_000
private const val SHORT_UPDATE_INTERVAL: Long = 2_000
private const val CANCELLATION_COOLDOWN: Long = 3_600_000 // 1 hour

class FacilityViewModel(
    facility: Facility,
    private val context: Application,
    private val interactor: FacilityInteractor,
    private val router: Router
) : BaseViewModel<ViewState>(), LifecycleEventObserver {

    init {
        processDataEvent(DataEvent.OnFacilityUpdate(facility))
    }

    private var timer: Timer? = null

    val singleEvent = SingleLiveEvent<SingleEvent>()

    override fun initialViewState() = ViewState(
        facility = createFacilityNull(),
        pendingArmingOrDisarming = false,
        isProgressBarShown = false,
        progressBarHintRes = null,
        alarmButtonIconRes = R.drawable.alarm_icon,
        alarmButtonColorRes = R.color.alarm_button_color
    )

    override suspend fun reduce(event: Event, previousState: ViewState): ViewState? {
        when (event) {
            is UiEvent.OnArmButtonLongClick -> {
                val facility = previousState.facility
                val isOnlineChannelAvailable = facility.hasOnlineChannel && facility.isOnlineChannelEnabled

                if (!isOnlineChannelAvailable) {
                    return null
                }

                if (!facility.isArmingEnabled) {
                    singleEvent.postValue(SingleEvent.OnError(R.string.arming_is_not_available_message))
                    return null
                }

                val confirmDialogEvent = SingleEvent.OnConfirmationDialog(
                    R.string.arming_perimeter_dialog_message,
                    R.string.arming_perimeter_dialog_positive_button_text,
                    UiEvent.OnPerimeterArmingConfirmed
                )

                singleEvent.postValue(confirmDialogEvent)
                return null
            }
            is UiEvent.OnArmButtonClick -> {
                val facility = previousState.facility
                val isOnlineChannelAvailable = facility.hasOnlineChannel && facility.isOnlineChannelEnabled

                if (!isOnlineChannelAvailable) {
                    return null
                }

                if (!facility.isArmingEnabled) {
                    singleEvent.postValue(SingleEvent.OnError(R.string.arming_is_not_available_message))
                    return null
                }

                val confirmDialogEvent = if (!facility.isGuarded) {
                    SingleEvent.OnConfirmationDialog(
                        R.string.arming_dialog_message,
                        R.string.arming_dialog_positive_button_text,
                        UiEvent.OnArmingConfirmed
                    )
                } else {
                    SingleEvent.OnConfirmationDialog(
                        R.string.disarming_dialog_message,
                        R.string.disarming_dialog_positive_button_text,
                        UiEvent.OnDisarmingConfirmed
                    )
                }

                singleEvent.postValue(confirmDialogEvent)
                return null
            }
            is UiEvent.OnArmingConfirmed -> {
                viewModelScope.launch {
                    interactor.arm(previousState.facility.id).fold(
                        onSuccess = { success ->
                            if (success) {
                                processDataEvent(DataEvent.OnArmingStart)
                            } else {
                                processDataEvent(DataEvent.OnArmingFail)
                            }
                        },
                        onError = {
                            processDataEvent(DataEvent.OnArmingFail)
                        }
                    )
                }

                return null
            }
            is UiEvent.OnPerimeterArmingConfirmed -> {
                viewModelScope.launch {
                    interactor.armPerimeter(previousState.facility.id).fold(
                        onSuccess = { success ->
                            if (success) {
                                processDataEvent(DataEvent.OnPerimeterArmingStart)
                            } else {
                                processDataEvent(DataEvent.OnPerimeterArmingFail)
                            }
                        },
                        onError = {
                            processDataEvent(DataEvent.OnPerimeterArmingFail)
                        }
                    )
                }

                return null
            }
            is UiEvent.OnDisarmingConfirmed -> {
                viewModelScope.launch {
                    interactor.disarm(previousState.facility.id).fold(
                        onSuccess = { success ->
                            if (success) {
                                processDataEvent(DataEvent.OnDisarmingStart)
                            } else {
                                processDataEvent(DataEvent.OnDisarmingFail)
                            }
                        },
                        onError = {
                            processDataEvent(DataEvent.OnDisarmingFail)
                        }
                    )
                }

                return null
            }
            is UiEvent.OnBackButtonClick -> {
                router.exit()
                return null
            }
            is UiEvent.OnRenameButtonClick -> {
                singleEvent.postValue(SingleEvent.OnRenameDialog(previousState.facility.name))
                return null
            }
            is UiEvent.OnApplyButtonClick -> {
                router.navigateTo(Screens.application(previousState.facility.id))
                return null
            }
            is UiEvent.OnRenameConfirmed -> {
                viewModelScope.launch {
                    interactor.setName(previousState.facility.id, event.name).fold(
                        onSuccess = { renamingResult ->
                            if (renamingResult.success) {
                                processDataEvent(DataEvent.OnRenamingResult(renamingResult.name ?: ""))
                            } else {
                                processDataEvent(DataEvent.OnRenamingFail)
                            }
                        },
                        onError = {
                            processDataEvent(DataEvent.OnRenamingFail)
                        }
                    )
                }
                return null
            }
            is UiEvent.OnAlarmButtonClick -> {
                val facility = previousState.facility

                if (facility.statusCodes.contains(StatusCode.ALARM)) {
                    val lastCancellationTime = interactor.getLastCancellationTime(previousState.facility.id)

                    if (lastCancellationTime != null) {
                        val timeSinceLastCancellation = elapsedRealtime() - lastCancellationTime

                        // If the timeSinceLastCancellation is negative, it means that device was
                        // rebooted and lastCancellationTime is invalid
                        if (timeSinceLastCancellation in 0 until CANCELLATION_COOLDOWN) {
                            singleEvent.postValue(SingleEvent.OnError(R.string.cancel_alarm_cooldown_message))
                            return null
                        }
                    }

                    interactor.setLastCancellationTime(previousState.facility.id, elapsedRealtime())

                    val passcode = facility.passcode

                    if (passcode == null) {
                        singleEvent.postValue(SingleEvent.OnError(R.string.cancel_alarm_passcode_not_found_message))
                        return null
                    }

                    val allPasscodes = context.resources.getStringArray(R.array.passcodes).toList()

                    val passcodes = mutableListOf(passcode).apply {
                        addAll(getRandomElements(3, allPasscodes))
                        shuffle()
                    }

                    singleEvent.postValue(SingleEvent.OnCancelAlarmDialog(passcodes))
                    return null
                }

                if (!facility.isAlarmButtonEnabled) {
                    singleEvent.postValue(SingleEvent.OnError(R.string.alarm_is_not_available_message))
                    return null
                }

                val confirmDialogEvent = SingleEvent.OnConfirmationDialog(
                    R.string.alarm_dialog_message,
                    R.string.alarm_dialog_positive_button_text,
                    UiEvent.OnAlarmConfirmed
                )

                singleEvent.postValue(confirmDialogEvent)
                return null
            }
            is UiEvent.OnAlarmConfirmed -> {
                val facility = previousState.facility

                viewModelScope.launch {
                    interactor.startAlarm(facility.id).fold(
                        onSuccess = { success ->
                            if (!success) {
                                processDataEvent(DataEvent.OnAlarmFail)
                            }
                        },
                        onError = {
                            processDataEvent(DataEvent.OnAlarmFail)
                        }
                    )
                }
                return null
            }
            is UiEvent.OnCancelAlarmConfirmed -> {
                val facility = previousState.facility

                viewModelScope.launch {
                    interactor.cancelAlarm(facility.id, event.passcode).fold(
                        onSuccess = { success ->
                            if (!success) {
                                processDataEvent(DataEvent.OnCancelAlarmFail)
                            }
                        },
                        onError = {
                            processDataEvent(DataEvent.OnCancelAlarmFail)
                        }
                    )
                }
                return null
            }
            is UiEvent.OnTestButtonClick -> {
                val facilityId = previousState.facility.id
                singleEvent.postValue(SingleEvent.OnTestModeDialog(facilityId))
                return null
            }
            is DataEvent.OnArmingStart -> {
                startTimer(SHORT_UPDATE_INTERVAL)

                return previousState.copy(
                    pendingArmingOrDisarming = true,
                    isProgressBarShown = true,
                    progressBarHintRes = R.string.arming_progress_bar_hint
                )
            }
            is DataEvent.OnArmingFail -> {
                singleEvent.postValue(SingleEvent.OnError(R.string.arming_failed_message))
                return null
            }
            is DataEvent.OnPerimeterArmingStart -> {
                startTimer(SHORT_UPDATE_INTERVAL)

                return previousState.copy(
                    pendingArmingOrDisarming = true,
                    isProgressBarShown = true,
                    progressBarHintRes = R.string.arming_perimeter_progress_bar_hint
                )
            }
            is DataEvent.OnPerimeterArmingFail -> {
                singleEvent.postValue(SingleEvent.OnError(R.string.arming_perimeter_failed_message))
                return null
            }
            is DataEvent.OnDisarmingStart -> {
                startTimer(SHORT_UPDATE_INTERVAL)

                return previousState.copy(
                    pendingArmingOrDisarming = true,
                    isProgressBarShown = true,
                    progressBarHintRes = R.string.disarming_progress_bar_hint
                )
            }
            is DataEvent.OnDisarmingFail -> {
                singleEvent.postValue(SingleEvent.OnError(R.string.disarming_failed_message))
                return null
            }
            is DataEvent.OnFacilityRequest -> {
                val facility = previousState.facility

                viewModelScope.launch {
                    interactor.getFacility(facility.id).fold(
                        onSuccess = { facility ->
                            processDataEvent(DataEvent.OnFacilityUpdate(facility))
                        },
                        onError = {}
                    )
                }

                return null
            }
            is DataEvent.OnFacilityUpdate -> {
                if (!event.facility.alarm) {
                    interactor.removeLastCancellationTime(event.facility.id)
                }

                if (previousState.pendingArmingOrDisarming) {
                    val facility = previousState.facility
                    val updatedFacility = event.facility

                    if (isArmingOrDisarmingComplete(facility, updatedFacility)) {
                        startTimer(LONG_UPDATE_INTERVAL)

                        return previousState.copy(
                            facility = event.facility,
                            pendingArmingOrDisarming = false,
                            isProgressBarShown = false,
                            progressBarHintRes = null,
                            alarmButtonIconRes = getAlarmButtonIcon(event.facility.alarm),
                            alarmButtonColorRes = getAlarmButtonColor(event.facility.alarm)
                        )
                    }
                }

                return previousState.copy(
                    facility = event.facility,
                    alarmButtonIconRes = getAlarmButtonIcon(event.facility.alarm),
                    alarmButtonColorRes = getAlarmButtonColor(event.facility.alarm)
                )
            }
            is DataEvent.OnRenamingResult -> {
                val updatedFacility = previousState.facility.copy(name = event.name)

                return previousState.copy(
                    facility = updatedFacility
                )
            }
            is DataEvent.OnRenamingFail -> {
                singleEvent.postValue(SingleEvent.OnError(R.string.renaming_failed_message))
                return null
            }
            is DataEvent.OnAlarmFail -> {
                singleEvent.postValue(SingleEvent.OnError(R.string.alarm_failed_message))
                return null
            }
            is DataEvent.OnCancelAlarmFail -> {
                singleEvent.postValue(SingleEvent.OnError(R.string.cancel_alarm_failed_message))
                return null
            }
            else -> return null
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                startTimer(LONG_UPDATE_INTERVAL)
            }
            Lifecycle.Event.ON_PAUSE -> {
                timer?.cancel()
            }
            else -> return
        }
    }

    private fun startTimer(period: Long) {
        timer?.cancel()
        timer = Timer().apply {
            schedule(delay = 0, period = period) {
                processDataEvent(DataEvent.OnFacilityRequest)
            }
        }
    }

    private fun isArmingOrDisarmingComplete(facility: Facility, updatedFacility: Facility): Boolean =
        !facility.isGuarded && updatedFacility.isGuarded ||
                facility.isGuarded && !updatedFacility.isGuarded

    private fun getAlarmButtonIcon(alarm: Boolean) = if (alarm) {
        R.drawable.cancel_alarm_icon
    } else {
        R.drawable.alarm_icon
    }

    private fun getAlarmButtonColor(alarm: Boolean) = if (alarm) {
        R.color.cancel_alarm_button_color
    } else {
        R.color.alarm_button_color
    }

}

private fun createFacilityNull() = Facility(
    id = "",
    name = "",
    address = "",
    statusCodes = listOf(StatusCode.UNKNOWN),
    statusDescription = "",
    isSelfServiceEnabled = false,
    hasOnlineChannel = false,
    isOnlineChannelEnabled = false,
    isArmingEnabled = false,
    isAlarmButtonEnabled = false,
    batteryMalfunction = false,
    powerSupplyMalfunction = false,
    passcode = null,
    isApplicationsEnabled = false,
    accounts = emptyList(),
    devices = emptyList()
)

private fun <T> getRandomElements(count: Int, source: List<T>): List<T> {
    if (source.count() <= count) {
        return source
    }

    val random = Random(System.currentTimeMillis())
    val indexesUsed = mutableSetOf<Int>()
    val result = mutableListOf<T>()

    while (result.count() < count) {
        val index = random.nextInt(0, source.count())

        if (!indexesUsed.contains(index)) {
            indexesUsed.add(index)
            result.add(source[index])
        }
    }

    return result
}