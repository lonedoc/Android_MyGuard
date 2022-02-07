package kobramob.rubeg38.ru.myprotection.feature.login.ui

import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.Screens
import kobramob.rubeg38.ru.myprotection.base.BaseViewModel
import kobramob.rubeg38.ru.myprotection.base.Event
import kobramob.rubeg38.ru.myprotection.domain.models.GuardService
import kobramob.rubeg38.ru.myprotection.feature.login.domain.LoginFormInteractor
import kobramob.rubeg38.ru.myprotection.utils.SingleLiveEvent
import kotlinx.coroutines.launch

private const val PHONE_NUMBER_LENGTH = 18 // "+7 (123) 456-78-90"

class LoginViewModel(
    private val interactor: LoginFormInteractor,
    private val router: Router
) : BaseViewModel<ViewState>() {

    val singleEvent = SingleLiveEvent<SingleEvent>()

    init {
        processDataEvent(DataEvent.OnCachedDataLoad)
        processDataEvent(DataEvent.OnCitiesRequest)
    }

    override fun initialViewState() = ViewState(
        cities = emptyList(),
        guardServices = emptyList(),
        selectedCity = null,
        selectedGuardService = null,
        phoneNumber = null,
        isSubmitButtonEnabled = false
    )

    override suspend fun reduce(event: Event, previousState: ViewState): ViewState? {
        when (event) {
            is UiEvent.OnCitySelected -> {
                if (previousState.selectedCity == event.city) {
                    return null
                }

                return previousState.copy(
                    guardServices = event.city?.guardServices ?: emptyList(),
                    selectedCity = event.city,
                    selectedGuardService = null,
                    isSubmitButtonEnabled = false
                )
            }
            is UiEvent.OnCompanySelected -> {
                val buttonEnabled = isSubmitButtonEnabled(
                    event.guardService,
                    previousState.phoneNumber
                )

                return previousState.copy(
                    selectedGuardService = event.guardService,
                    isSubmitButtonEnabled = buttonEnabled
                )
            }
            is UiEvent.OnPhoneNumberEntered -> {
                if (event.phoneNumber == previousState.phoneNumber) {
                    return null
                }

                val buttonEnabled = isSubmitButtonEnabled(
                    previousState.selectedGuardService,
                    event.phoneNumber
                )

                return previousState.copy(
                    phoneNumber = event.phoneNumber,
                    isSubmitButtonEnabled = buttonEnabled
                )
            }
            is UiEvent.OnProceedButtonClicked -> {
                val city = previousState.selectedCity!!
                val guardService = previousState.selectedGuardService!!
                val phoneNumber = previousState.phoneNumber!!

                interactor.saveCityName(city.name)
                interactor.saveGuardService(guardService)
                interactor.savePhoneNumber(phoneNumber)

                val passwordScreen = Screens.password(guardService, phoneNumber)
                router.navigateTo(passwordScreen)

                return null
            }
            is DataEvent.OnCachedDataLoad -> {
                val phoneNumber = interactor.getCachedPhoneNumber()
                return previousState.copy(phoneNumber = phoneNumber)
            }
            is DataEvent.OnCitiesRequest -> {
                viewModelScope.launch {
                    interactor.getCities().fold(
                        onSuccess = { cities ->
                            processDataEvent(DataEvent.OnSuccessCitiesRequest(cities))
                        },
                        onError = {
                            processDataEvent(DataEvent.OnFailureCitiesRequest)
                        }
                    )
                }

                return null
            }
            is DataEvent.OnSuccessCitiesRequest -> {
                val selectedCityName = interactor.getCachedCityName()
                val selectedGuardServiceName = interactor.getCachedGuardService()?.name

                val selectedCity = event.cities.firstOrNull { city -> city.name == selectedCityName }
                val guardServices = selectedCity?.guardServices ?: emptyList()

                val selectedGuardService = guardServices.firstOrNull { guardService ->
                    guardService.name == selectedGuardServiceName
                }

                val buttonEnabled = isSubmitButtonEnabled(
                    selectedGuardService,
                    previousState.phoneNumber
                )

                return previousState.copy(
                    cities = event.cities,
                    selectedCity = selectedCity,
                    guardServices = guardServices,
                    selectedGuardService = selectedGuardService,
                    isSubmitButtonEnabled = buttonEnabled
                )
            }
            is DataEvent.OnFailureCitiesRequest -> {
                val errorMessageRes = R.string.request_failed_message
                singleEvent.postValue(SingleEvent.OnShowErrorMessage(errorMessageRes))
                return null
            }
            else -> return null
        }
    }

    private fun isSubmitButtonEnabled(selectedGuardService: GuardService?, phoneNumber: String?): Boolean =
        selectedGuardService != null && phoneNumber?.count() == PHONE_NUMBER_LENGTH

}