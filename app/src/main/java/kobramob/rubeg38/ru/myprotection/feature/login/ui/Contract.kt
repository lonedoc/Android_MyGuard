package kobramob.rubeg38.ru.myprotection.feature.login.ui

import androidx.annotation.StringRes
import kobramob.rubeg38.ru.myprotection.base.Event
import kobramob.rubeg38.ru.myprotection.domain.models.GuardService
import kobramob.rubeg38.ru.myprotection.feature.login.domain.models.City

data class ViewState(
    val cities: List<City>,
    val guardServices: List<GuardService>,
    val selectedCity: City?,
    val selectedGuardService: GuardService?,
    val phoneNumber: String?,
    val isSubmitButtonEnabled: Boolean
)

sealed class UiEvent : Event {
    object OnProceedButtonClicked : UiEvent()
    data class OnCitySelected(val city: City?) : UiEvent()
    data class OnCompanySelected(val guardService: GuardService?) : UiEvent()
    data class OnPhoneNumberEntered(val phoneNumber: String?) : UiEvent()
}

sealed class DataEvent : Event {
    object OnCachedDataLoad : DataEvent()
    object OnCitiesRequest : DataEvent()
    object OnFailureCitiesRequest : DataEvent()
    data class OnSuccessCitiesRequest(val cities: List<City>) : DataEvent()
}

sealed class SingleEvent {
    data class OnShowErrorMessage(@StringRes val errorMessageRes: Int) : SingleEvent()
}