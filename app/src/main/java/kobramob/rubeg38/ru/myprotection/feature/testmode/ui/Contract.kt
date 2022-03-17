package kobramob.rubeg38.ru.myprotection.feature.testmode.ui

import androidx.annotation.StringRes
import kobramob.rubeg38.ru.myprotection.base.Event
import kobramob.rubeg38.ru.myprotection.feature.testmode.domain.models.TestingStatus

data class ViewState(
    @StringRes val hintRes: Int?,
    val countDownText: String,
    val isResetButtonEnabled: Boolean
)

sealed class UiEvent : Event {
    object OnResetButtonClick : UiEvent()
    object OnEndTestingButtonClick : UiEvent()
}

sealed class DataEvent : Event {
    object OnStartTestMode : DataEvent()
    object OnTestModeRequestFailed : DataEvent()
    object OnEndTestMode : DataEvent()
    object OnTestModeEnded : DataEvent()
    object OnEndTestModeRequestFailed : DataEvent()
    object OnReset : DataEvent()
    object OnResetFailed : DataEvent()
    object OnStatusRequest : DataEvent()
    object OnCountDownFinished : DataEvent()
    data class OnTestModeStarted(val duration: Long) : DataEvent()
    data class OnCountDownTick(val timeLeft: Long) : DataEvent()
    data class OnStatusReceived(val testingStatus: TestingStatus) : DataEvent()
}

sealed class SingleEvent {
    object OnDismissDialog : SingleEvent()
    data class OnError(@StringRes val errorMessageRes: Int) : SingleEvent()
}