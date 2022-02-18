package kobramob.rubeg38.ru.myprotection.feature.password.ui

import android.os.Build
import android.os.CountDownTimer
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.Screens
import kobramob.rubeg38.ru.myprotection.base.BaseViewModel
import kobramob.rubeg38.ru.myprotection.base.Event
import kobramob.rubeg38.ru.myprotection.domain.LoginInteractor
import kobramob.rubeg38.ru.myprotection.domain.models.Credentials
import kobramob.rubeg38.ru.myprotection.domain.models.GuardService
import kobramob.rubeg38.ru.myprotection.feature.password.domain.PasswordInteractor
import kobramob.rubeg38.ru.myprotection.utils.SingleLiveEvent
import kotlinx.coroutines.launch

private const val COOLDOWN_MILLIS: Long = 15_000
private const val COUNTDOWN_INTERVAL: Long = 1000

class PasswordViewModel(
    private val guardService: GuardService,
    private val phoneNumber: String,
    private val passwordInteractor: PasswordInteractor,
    private val loginInteractor: LoginInteractor,
    private val router: Router
) : BaseViewModel<ViewState>() {

    private var timer: CountDownTimer? = null

    init {
        viewModelScope.launch {
            passwordInteractor.setIpAddresses(guardService.addresses)
        }

        processUiEvent(UiEvent.OnPasswordRequest)
    }

    val singleEvent = SingleLiveEvent<SingleEvent>()

    override fun initialViewState() = ViewState(
        password = "",
        countDownValue = COOLDOWN_MILLIS / 1000,
        isCountDownShown = true,
        isSubmitButtonEnabled = false
    )

    override suspend fun reduce(event: Event, previousState: ViewState): ViewState? {
        when (event) {
            is UiEvent.OnPasswordRequest -> {
                startCountDownTimer()

                viewModelScope.launch {
                    passwordInteractor.requestPassword(phoneNumber).fold(
                        onSuccess = { },
                        onError = {
                            processDataEvent(DataEvent.OnPasswordRequestFail)
                        }
                    )
                }
                return null
            }
            is UiEvent.OnCancelButtonClick -> {
                router.exit()
                return null
            }
            is UiEvent.OnFcmTokenRequestFail -> {
                val errorEvent = SingleEvent.OnError(R.string.fcm_token_unavailable_message)
                singleEvent.postValue(errorEvent)
                return null
            }
            is UiEvent.OnReadyToLogin -> {
                val credentials = Credentials(phoneNumber, previousState.password)
                val deviceName = Build.MODEL

                viewModelScope.launch {
                    loginInteractor.login(credentials, event.token, deviceName).fold(
                        onSuccess = { loginResponse ->
                            processDataEvent(DataEvent.OnLoginResponseReceived(loginResponse))
                        },
                        onError = {
                            processDataEvent(DataEvent.OnLoginRequestFail)
                        }
                    )
                }

                return null
            }
            is UiEvent.OnPasswordChange -> {
                return previousState.copy(
                    password = event.password,
                    isSubmitButtonEnabled = event.password.isNotBlank()
                )
            }
            is DataEvent.OnPasswordRequestFail -> {
                stopCountDownTimer()

                val errorEvent = SingleEvent.OnError(R.string.password_request_failed_message)
                singleEvent.postValue(errorEvent)

                return previousState.copy(
                    isCountDownShown = false
                )
            }
            is DataEvent.OnCountDownTimerTick -> {
                return previousState.copy(
                    countDownValue = event.timeLeft,
                    isCountDownShown = event.timeLeft > 0
                )
            }
            is DataEvent.OnLoginRequestFail -> {
                val errorEvent = SingleEvent.OnError(R.string.login_failed_message)
                singleEvent.postValue(errorEvent)
                return null
            }
            is DataEvent.OnLoginResponseReceived -> {
                passwordInteractor.saveToken(event.loginResponse.token)
                passwordInteractor.saveUser(event.loginResponse.user)
                passwordInteractor.updateGuardService(event.loginResponse.guardService)

                passwordInteractor.setToken(event.loginResponse.token)

                router.navigateTo(Screens.passcode())

                return null
            }
            else -> return null
        }
    }

    private fun startCountDownTimer() {
        timer?.cancel()

        timer = object : CountDownTimer(COOLDOWN_MILLIS, COUNTDOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = millisUntilFinished / 1000
                processDataEvent(DataEvent.OnCountDownTimerTick(timeLeft))
            }

            override fun onFinish() {}
        }

        timer?.start()
    }

    private fun stopCountDownTimer() {
        timer?.cancel()
    }

}