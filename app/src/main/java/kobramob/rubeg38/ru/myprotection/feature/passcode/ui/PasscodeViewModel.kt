package kobramob.rubeg38.ru.myprotection.feature.passcode.ui

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kobramob.rubeg38.ru.myprotection.BuildConfig
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.Screens
import kobramob.rubeg38.ru.myprotection.base.BaseViewModel
import kobramob.rubeg38.ru.myprotection.base.Event
import kobramob.rubeg38.ru.myprotection.feature.passcode.domain.PasscodeInteractor
import kobramob.rubeg38.ru.myprotection.utils.SingleLiveEvent
import kotlinx.coroutines.launch

private const val PASSCODE_LENGTH = 4

class PasscodeViewModel(
    private val interactor: PasscodeInteractor,
    private val router: Router
) : BaseViewModel<ViewState>() {

    val singleEvent = SingleLiveEvent<SingleEvent>()

    init {
        processDataEvent(DataEvent.OnCachedPasscodeRequest)
        processDataEvent(DataEvent.OnBiometryAvailabilityCheck)
    }

    override fun initialViewState() = ViewState(
        stage = Stage.CREATION,
        isBiometricReady = false,
        isBiometricButtonVisible = false,
        isForgotCodeButtonVisible = false,
        isAppBarMenuVisible = false,
        hintTextRes = R.string.passcode_creation_hint_text,
        passcode1 = "",
        passcode2 = "",
        indicatorValue = 0,
        version = BuildConfig.VERSION_NAME
    )

    override suspend fun reduce(event: Event, previousState: ViewState): ViewState? {
        when (event) {
            is UiEvent.OnExitButtonClick -> {
                viewModelScope.launch {
                    interactor.logout().fold(
                        onSuccess = { result ->
                            if (result) {
                                processDataEvent(DataEvent.OnLoggedOut)
                            } else {
                                processDataEvent(DataEvent.OnLogoutFailed)
                            }
                        },
                        onError = { processDataEvent(DataEvent.OnLogoutFailed) }
                    )
                }

                return null
            }
            is UiEvent.OnCallButtonClick -> {
                val phoneNumber: String? = interactor.getGuardServicePhoneNumber()

                if (phoneNumber == null) {
                    val errorMessageRes = R.string.phone_number_not_found_message
                    singleEvent.postValue(SingleEvent.OnError(errorMessageRes))
                    return null
                }

                val phoneNumberUri = Uri.parse("tel:$phoneNumber")
                singleEvent.postValue(SingleEvent.OnCall(phoneNumberUri))
                return null
            }
            is UiEvent.OnForgotCodeButtonClick -> {
                interactor.removePasscode()

                val guardService = interactor.getGuardService()
                val userPhoneNumber = interactor.getUserPhoneNumber()

                if (guardService == null || userPhoneNumber == null) {
                    router.newRootScreen(Screens.login())
                    return null
                }

                router.newRootScreen(Screens.password(guardService, userPhoneNumber))
                return null
            }
            is UiEvent.OnBiometricButtonClick -> {
                singleEvent.postValue(SingleEvent.OnShowBiometricPrompt)
                return null
            }
            is UiEvent.OnBiometricAuthSuccess -> {
                router.newRootScreen(Screens.facilities())
                return null
            }
            is UiEvent.OnCharacterAdded -> {
                val passcode = previousState.passcode1 + event.character

                if (passcode.count() > PASSCODE_LENGTH) {
                    return null
                }

                if (passcode.count() < PASSCODE_LENGTH) {
                    return previousState.copy(
                        passcode1 = passcode,
                        indicatorValue = passcode.count()
                    )
                }

                when (previousState.stage) {
                    Stage.CREATION -> {
                        return previousState.copy(
                            stage = Stage.CONFIRMATION,
                            hintTextRes = R.string.passcode_confirmation_hint_text,
                            passcode1 = "",
                            passcode2 = passcode,
                            indicatorValue = 0
                        )
                    }
                    Stage.CONFIRMATION -> {
                        if (passcode == previousState.passcode2) {
                            interactor.savePasscode(passcode)

                            router.newRootScreen(Screens.facilities())

                            return previousState.copy(
                                passcode1 = passcode,
                                indicatorValue = passcode.count()
                            )
                        } else {
                            val errorMessageRes = R.string.wrong_passcode_confirmation_message
                            singleEvent.postValue(SingleEvent.OnError(errorMessageRes))

                            return previousState.copy(
                                stage = Stage.CREATION,
                                hintTextRes = R.string.passcode_creation_hint_text,
                                passcode1 = "",
                                passcode2 = "",
                                indicatorValue = 0
                            )
                        }
                    }
                    Stage.ENTRANCE -> {
                        if (passcode == previousState.passcode2) {
                            router.newRootScreen(Screens.facilities())

                            return previousState.copy(
                                passcode1 = passcode,
                                indicatorValue = passcode.count()
                            )
                        } else {
                            val errorMessageRes = R.string.wrong_passcode_message
                            singleEvent.postValue(SingleEvent.OnError(errorMessageRes))

                            return previousState.copy(
                                passcode1 = "",
                                indicatorValue = 0
                            )
                        }
                    }
                }
            }
            is UiEvent.OnCharacterRemoved -> {
                return if (previousState.passcode1.isEmpty()) {
                    null
                } else {
                    val passcode = previousState.passcode1.substring(
                        0 until previousState.passcode1.count() - 1
                    )

                    previousState.copy(
                        passcode1 = passcode,
                        indicatorValue = passcode.count()
                    )
                }
            }
            is DataEvent.OnBiometryAvailabilityCheck -> {
                val isEntranceStage = previousState.stage == Stage.ENTRANCE
                val isBiometricReady = interactor.isBiometricReady()

                return previousState.copy(
                    isBiometricReady = isBiometricReady,
                    isBiometricButtonVisible = isEntranceStage && isBiometricReady
                )
            }
            is DataEvent.OnLoggedOut -> {
                router.newRootScreen(Screens.login())
                return null
            }
            is DataEvent.OnLogoutFailed -> {
                singleEvent.postValue(SingleEvent.OnError(R.string.logout_failed_message))
                return null
            }
            is DataEvent.OnCachedPasscodeRequest -> {
                viewModelScope.launch {
                    val passcode = interactor.getPasscode()
                    processDataEvent(DataEvent.OnCachedPasscodeReceived(passcode))
                }
                return null
            }
            is DataEvent.OnCachedPasscodeReceived -> {
                val stage = if (event.passcode == null) {
                    Stage.CREATION
                } else {
                    Stage.ENTRANCE
                }

                val isPasscodeCreated = event.passcode != null

                val hintTextRes = if (event.passcode == null) {
                    R.string.passcode_creation_hint_text
                } else {
                    R.string.entrance_with_passcode_hint_text
                }

                val isEntranceStage = stage == Stage.ENTRANCE
                val isBiometricReady = previousState.isBiometricReady

                return previousState.copy(
                    stage = stage,
                    isBiometricButtonVisible = isEntranceStage && isBiometricReady,
                    isForgotCodeButtonVisible = isPasscodeCreated,
                    isAppBarMenuVisible = isPasscodeCreated,
                    hintTextRes = hintTextRes,
                    passcode1 = "",
                    passcode2 = event.passcode ?: "",
                    indicatorValue = 0
                )
            }
            else -> return null
        }
    }

}