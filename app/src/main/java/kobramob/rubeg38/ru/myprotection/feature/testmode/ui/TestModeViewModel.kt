package kobramob.rubeg38.ru.myprotection.feature.testmode.ui

import android.os.CountDownTimer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.base.BaseViewModel
import kobramob.rubeg38.ru.myprotection.base.Event
import kobramob.rubeg38.ru.myprotection.feature.testmode.domain.TestModeInteractor
import kobramob.rubeg38.ru.myprotection.feature.testmode.domain.models.TestingStatus
import kobramob.rubeg38.ru.myprotection.utils.SingleLiveEvent
import kobramob.rubeg38.ru.myprotection.utils.schedule
import kotlinx.coroutines.launch
import java.util.*

private const val UPDATE_INTERVAL: Long = 2_000
private const val COUNTDOWN_INTERVAL: Long = 1000

class TestModeViewModel(
    private val facilityId: String,
    private val interactor: TestModeInteractor
) : BaseViewModel<ViewState>(), LifecycleEventObserver {

    private var timer: Timer? = null
    private var countDownTimer: CountDownTimer? = null

    val singleEvent = SingleLiveEvent<SingleEvent>()

    init {
        processDataEvent(DataEvent.OnStartTestMode)
    }

    override fun initialViewState() = ViewState(
        hintRes = null,
        countDownText = "",
        isResetButtonEnabled = false
    )

    override suspend fun reduce(event: Event, previousState: ViewState): ViewState? {
        when (event) {
            is UiEvent.OnEndTestingButtonClick -> {
                processDataEvent(DataEvent.OnEndTestMode)
                return null
            }
            is UiEvent.OnResetButtonClick -> {
                viewModelScope.launch {
                    interactor.reset(facilityId).fold(
                        onSuccess = { success ->
                            val eventToProcess = if (success) {
                                DataEvent.OnReset
                            } else {
                                DataEvent.OnResetFailed
                            }

                            processDataEvent(eventToProcess)
                        },
                        onError = {
                            processDataEvent(DataEvent.OnResetFailed)
                        }
                    )
                }

                return null
            }
            is DataEvent.OnReset -> {
                return previousState.copy(isResetButtonEnabled = false)
            }
            is DataEvent.OnResetFailed -> {
                val errorMessageRes = R.string.reset_failed_message
                singleEvent.postValue(SingleEvent.OnError(errorMessageRes))
                return null
            }
            is DataEvent.OnEndTestMode -> {
                viewModelScope.launch {
                    interactor.endTesting(facilityId).fold(
                        onSuccess = { success ->
                            val eventToProcess = if (success) {
                                DataEvent.OnTestModeEnded
                            } else {
                                DataEvent.OnTestModeRequestFailed
                            }

                            processDataEvent(eventToProcess)
                        },
                        onError = {
                            processDataEvent(DataEvent.OnTestModeRequestFailed)
                        }
                    )
                }

                return null
            }
            is DataEvent.OnTestModeEnded -> {
                singleEvent.postValue(SingleEvent.OnDismissDialog)
                return null
            }
            is DataEvent.OnEndTestModeRequestFailed -> {
                val errorMessageRes = R.string.end_testing_failed_message
                singleEvent.postValue(SingleEvent.OnError(errorMessageRes))
                singleEvent.postValue(SingleEvent.OnDismissDialog)
                return null
            }
            is DataEvent.OnStartTestMode -> {
                viewModelScope.launch {
                    interactor.startTesting(facilityId).fold(
                        onSuccess = { result ->
                            if (result.isStarted && result.timeLeft != null) {
                                processDataEvent(DataEvent.OnTestModeStarted(result.timeLeft))
                            } else {
                                processDataEvent(DataEvent.OnTestModeRequestFailed)
                            }
                        },
                        onError = {
                            processDataEvent(DataEvent.OnTestModeRequestFailed)
                        }
                    )
                }

                return null
            }
            is DataEvent.OnTestModeRequestFailed -> {
                val errorMessageRes = R.string.testing_failed_message
                singleEvent.postValue(SingleEvent.OnError(errorMessageRes))
                singleEvent.postValue(SingleEvent.OnDismissDialog)
                return null
            }
            is DataEvent.OnTestModeStarted -> {
                timer?.cancel()
                timer = Timer().apply {
                    schedule(delay = 0, period = UPDATE_INTERVAL) {
                        processDataEvent(DataEvent.OnStatusRequest)
                    }
                }

                startCountDownTimer(event.duration * 1000)

                return previousState.copy(
                    hintRes = R.string.press_alarm_button_hint,
                    countDownText = formatDuration(event.duration)
                )
            }
            is DataEvent.OnCountDownTick -> {
                return previousState.copy(countDownText = formatDuration(event.timeLeft))
            }
            is DataEvent.OnCountDownFinished -> {
                processDataEvent(DataEvent.OnEndTestMode)
                return null
            }
            is DataEvent.OnStatusRequest -> {
                viewModelScope.launch {
                    interactor.getTestingStatus(facilityId).fold(
                        onSuccess = { status ->
                            processDataEvent(DataEvent.OnStatusReceived(status))
                        },
                        onError = { }
                    )
                }

                return null
            }
            is DataEvent.OnStatusReceived -> {
                val status = event.testingStatus

                return previousState.copy(
                    hintRes = getHintRes(status),
                    isResetButtonEnabled =
                        status.isAlarmButtonPressed && !status.isAlarmButtonReinstalled
                )
            }
            else -> return null
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE -> {
                processDataEvent(DataEvent.OnEndTestMode)
                timer?.cancel()
                countDownTimer?.cancel()
            }
            else -> return
        }
    }

    private fun startCountDownTimer(duration: Long) {
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(duration, COUNTDOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = millisUntilFinished / 1000
                processDataEvent(DataEvent.OnCountDownTick(timeLeft))
            }

            override fun onFinish() {
                processDataEvent(DataEvent.OnCountDownFinished)
            }
        }

        countDownTimer?.start()
    }

    private fun formatDuration(duration: Long): String {
        val minutes = duration / 60
        val seconds = duration % 60
        val leadingZero = if (seconds < 10) "0" else ""
        return "$minutes:$leadingZero$seconds"
    }

    private fun getHintRes(status: TestingStatus): Int {
        if (status.isAlarmButtonReinstalled) {
            return R.string.alarm_button_tested_hint
        }

        if (status.isAlarmButtonPressed) {
            return R.string.alarm_button_pressed_hint
        }

        return R.string.press_alarm_button_hint
    }

}