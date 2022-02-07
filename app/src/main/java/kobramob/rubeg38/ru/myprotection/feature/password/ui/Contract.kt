package kobramob.rubeg38.ru.myprotection.feature.password.ui

import androidx.annotation.StringRes
import kobramob.rubeg38.ru.myprotection.base.Event
import kobramob.rubeg38.ru.myprotection.domain.models.LoginResponse

data class ViewState(
    val password: String,
    val countDownValue: Long,
    val isCountDownShown: Boolean,
    val isSubmitButtonEnabled: Boolean
)

sealed class UiEvent : Event {
    object OnPasswordRequest : UiEvent()
    object OnCancelButtonClick : UiEvent()
    object OnFcmTokenRequestFail : UiEvent()
    data class OnReadyToLogin(val token: String) : UiEvent()
    data class OnPasswordChange(val password: String) : UiEvent()
}

sealed class DataEvent : Event {
    object OnPasswordRequestFail : DataEvent()
    object OnLoginRequestFail : DataEvent()
    data class OnCountDownTimerTick(val timeLeft: Long) : DataEvent()
    data class OnLoginResponseReceived(val loginResponse: LoginResponse) : DataEvent()
}

sealed class SingleEvent {
    data class OnError(@StringRes val errorMessageRes: Int) : SingleEvent()
}
