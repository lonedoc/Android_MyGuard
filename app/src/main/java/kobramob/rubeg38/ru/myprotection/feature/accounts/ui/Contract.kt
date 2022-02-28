package kobramob.rubeg38.ru.myprotection.feature.accounts.ui

import androidx.annotation.StringRes
import kobramob.rubeg38.ru.myprotection.base.Event
import kobramob.rubeg38.ru.myprotection.domain.models.Account

data class ViewState(
    val accounts: List<Account>,
    val selectedAccount: Account?,
    val sum: String,
    @StringRes val invalidSumMessageRes: Int?,
    val isPayButtonEnabled: Boolean
)

sealed class UiEvent : Event {
    object OnPayButtonClick : UiEvent()
    data class OnAccountSelect(val account: Account) : UiEvent()
    data class OnSumChange(val sum: String) : UiEvent()
}

sealed class DataEvent : Event {
    data class OnAccountsUpdate(val accounts: List<Account>) : DataEvent()
}