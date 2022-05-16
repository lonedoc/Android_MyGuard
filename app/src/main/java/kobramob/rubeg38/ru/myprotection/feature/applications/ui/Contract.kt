package kobramob.rubeg38.ru.myprotection.feature.applications.ui

import androidx.annotation.StringRes
import kobramob.rubeg38.ru.myprotection.base.Event
import java.util.*

data class ViewState(
    val applications: List<String>,
    val selectedApplication: String?,
    val applicationText: String,
    val date: Date?,
    val time: Date?,
    val dateText: String,
    val timeText: String,
    val isApplicationTextFieldEnabled: Boolean,
    val isSubmitButtonEnabled: Boolean
)

sealed class UiEvent : Event {
    object OnDateFieldClick : UiEvent()
    object OnTimeFieldClick : UiEvent()
    object OnSubmitButtonClick : UiEvent()
    object OnCloseButtonClick : UiEvent()
    data class OnApplicationSelected(val text: String) : UiEvent()
    data class OnApplicationTextChanged(val text: String) : UiEvent()
    data class OnDateChanged(val date: Date) : UiEvent()
    data class OnTimeChanged(val time: Date) : UiEvent()
}

sealed class DataEvent : Event {
    object OnApplicationsRequest : DataEvent()
    object OnFailureApplicationsRequest : DataEvent()
    data class OnUpdateApplications(val applications: List<String>) : DataEvent()
}

sealed class SingleEvent {
    data class OnDatePickerDialog(val initialValue: Date) : SingleEvent()
    data class OnTimePickerDialog(val initialValue: Date) : SingleEvent()
    data class OnError(val errorMessage: String) : SingleEvent()
}