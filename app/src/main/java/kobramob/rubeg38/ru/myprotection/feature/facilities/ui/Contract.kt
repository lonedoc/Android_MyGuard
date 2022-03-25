package kobramob.rubeg38.ru.myprotection.feature.facilities.ui

import androidx.annotation.StringRes
import kobramob.rubeg38.ru.myprotection.base.Event
import kobramob.rubeg38.ru.myprotection.domain.models.Facility
import kobramob.rubeg38.ru.myprotection.feature.facilities.domain.models.Sorting

data class ViewState(
    val isPlaceholderShown: Boolean,
    val isRefresherShown: Boolean,
    val isUserInitiatedRequestExecuting: Boolean,
    val facilities: List<Facility>,
    val sorting: Sorting,
    val comparator: FacilityComparator
)

sealed class UiEvent : Event {
    object OnFacilitiesRequest : UiEvent()
    object OnCallButtonClick : UiEvent()
    object OnSortButtonClick : UiEvent()
    data class OnSortingChanged(val sorting: Sorting) : UiEvent()
    data class OnFacilityItemClick(val facility: Facility) : UiEvent()
}

sealed class DataEvent : Event {
    object OnFacilitiesRequest : DataEvent()
    object OnFacilitiesRequestFail : DataEvent()
    data class OnFacilitiesUpdate(val facilities: List<Facility>) : DataEvent()
}

sealed class SingleEvent {
    data class OnSortingDialog(val sorting: Sorting) : SingleEvent()
    data class OnError(@StringRes val errorMessageRes: Int) : SingleEvent()
}