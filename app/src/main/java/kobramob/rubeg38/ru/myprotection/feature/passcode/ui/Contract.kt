package kobramob.rubeg38.ru.myprotection.feature.passcode.ui

import android.net.Uri
import androidx.annotation.StringRes
import kobramob.rubeg38.ru.myprotection.base.Event

enum class Stage {
    CREATION, CONFIRMATION, ENTRANCE
}

data class ViewState(
    val stage: Stage,
    val isForgotCodeButtonVisible: Boolean,
    val isAppBarMenuVisible: Boolean,
    @StringRes val hintTextRes: Int,
    val passcode1: String,
    val passcode2: String,
    val indicatorValue: Int,
    val version: String
)

sealed class UiEvent : Event {
    object OnExitButtonClick : UiEvent()
    object OnCallButtonClick : UiEvent()
    object OnForgotCodeButtonClick : UiEvent()
    object OnCharacterRemoved : UiEvent()
    data class OnCharacterAdded(val character: String) : UiEvent()
}

sealed class DataEvent : Event {
    object OnLoggedOut : DataEvent()
    object OnLogoutFailed : DataEvent()
    object OnCachedPasscodeRequest : DataEvent()
    data class OnCachedPasscodeReceived(val passcode: String?) : DataEvent()
}

sealed class SingleEvent {
    data class OnError(@StringRes val errorMessageRes: Int) : SingleEvent()
    data class OnCall(val uri: Uri) : SingleEvent()
}