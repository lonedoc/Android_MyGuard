package kobramob.rubeg38.ru.myprotection.feature.facility.ui

import androidx.annotation.StringRes
import kobramob.rubeg38.ru.myprotection.base.Event
import kobramob.rubeg38.ru.myprotection.domain.models.Facility

data class ViewState(
    val facility: Facility,
    val pendingArmingOrDisarming: Boolean,
    val isProgressBarShown: Boolean,
    @StringRes val progressBarHintRes: Int?
)

sealed class UiEvent : Event {
    object OnArmButtonLongClick : UiEvent()
    object OnPerimeterArmingConfirmed : UiEvent()
    object OnArmButtonClick : UiEvent()
    object OnArmingConfirmed : UiEvent()
    object OnDisarmingConfirmed : UiEvent()
    object OnBackButtonClick : UiEvent()
    object OnRenameButtonClick : UiEvent()
    object OnApplyButtonClick : UiEvent()
    object OnAlarmButtonClick : UiEvent()
    object OnAlarmConfirmed : UiEvent()
    object OnTestButtonClick : UiEvent()
    data class OnCancelAlarmConfirmed(val passcode: String) : UiEvent()
    data class OnRenameConfirmed(val name: String) : UiEvent()
}

sealed class DataEvent : Event {
    object OnFacilityRequest : DataEvent()
    object OnArmingStart : DataEvent()
    object OnArmingFail : DataEvent()
    object OnPerimeterArmingStart : DataEvent()
    object OnPerimeterArmingFail : DataEvent()
    object OnDisarmingStart : DataEvent()
    object OnDisarmingFail : DataEvent()
    object OnRenamingFail : DataEvent()
    object OnAlarmFail : DataEvent()
    object OnCancelAlarmFail : DataEvent()
    data class OnFacilityUpdate(val facility: Facility) : DataEvent()
    data class OnRenamingResult(val name: String) : DataEvent()
}

sealed class SingleEvent {
    data class OnConfirmationDialog(
        @StringRes val messageRes: Int,
        @StringRes val positiveButtonTextRes: Int,
        val uiEvent: UiEvent
    ) : SingleEvent()

    data class OnCancelAlarmDialog(val passcodes: List<String>) : SingleEvent()
    data class OnTestModeDialog(val facilityId: String) : SingleEvent()
    data class OnRenameDialog(val currentName: String) : SingleEvent()
    data class OnError(@StringRes val errorMessageRes: Int) : SingleEvent()
}