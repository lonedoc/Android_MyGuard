package kobramob.rubeg38.ru.myprotection.feature.accounts.ui

import com.github.terrakok.cicerone.Router
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.Screens
import kobramob.rubeg38.ru.myprotection.base.BaseViewModel
import kobramob.rubeg38.ru.myprotection.base.Event
import kobramob.rubeg38.ru.myprotection.domain.models.Account

class AccountsViewModel(
    accounts: List<Account>,
    private val router: Router
) : BaseViewModel<ViewState>() {

    init {
        processDataEvent(DataEvent.OnAccountsUpdate(accounts))
    }

    override fun initialViewState() = ViewState(
        accounts = emptyList(),
        selectedAccount = null,
        sum = "0.0",
        invalidSumMessageRes = null,
        isPayButtonEnabled = false
    )

    override suspend fun reduce(event: Event, previousState: ViewState): ViewState? {
        when (event) {
            is UiEvent.OnAccountSelect -> {
                return previousState.copy(
                    selectedAccount = event.account,
                    isPayButtonEnabled = isUserInputValid(
                        event.account,
                        event.account.monthlyPayment
                    ),
                    sum = event.account.monthlyPayment?.toString() ?: "0.0"
                )
            }
            is UiEvent.OnSumChange -> {
                return if (event.sum.toDoubleOrNull() == null) {
                    previousState.copy(
                        sum = event.sum,
                        invalidSumMessageRes = R.string.invalid_input_message,
                        isPayButtonEnabled = false
                    )
                } else {
                    previousState.copy(
                        sum = event.sum,
                        invalidSumMessageRes = null,
                        isPayButtonEnabled = isUserInputValid(
                            previousState.selectedAccount,
                            event.sum.toDoubleOrNull()
                        )
                    )
                }
            }
            is UiEvent.OnPayButtonClick -> {
                val account = previousState.selectedAccount ?: return null
                val sum = previousState.sum

                router.navigateTo(Screens.paymentPage(account, sum))
                return null
            }
            is DataEvent.OnAccountsUpdate -> {
                return previousState.copy(accounts = event.accounts)
            }
            else -> return null
        }
    }

    private fun isUserInputValid(selectedAccount: Account?, sum: Double?): Boolean =
        selectedAccount != null && sum != null && sum > 0.0

}