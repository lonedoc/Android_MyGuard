package kobramob.rubeg38.ru.myprotection.feature.splashscreen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kobramob.rubeg38.ru.myprotection.Screens
import kobramob.rubeg38.ru.myprotection.feature.splashscreen.domain.SplashScreenInteractor
import kobramob.rubeg38.ru.myprotection.utils.SessionDataHolder
import kotlinx.coroutines.launch
import ru.rubeg38.protocolclient.Address

private const val PORT = 8301

class SplashScreenViewModel(
    private val interactor: SplashScreenInteractor,
    private val sessionDataHolder: SessionDataHolder,
    private val router: Router
) : ViewModel() {

    fun onViewCreated() {
        val cityName = interactor.getCityName()
        val guardServiceName = interactor.getGuardServiceName()
        val userPhoneNumber = interactor.getUserPhoneNumber()
        val token = interactor.getToken()

        val isUserLoggedIn =
            cityName != null &&
            guardServiceName != null &&
            userPhoneNumber != null &&
            token != null

        if (isUserLoggedIn) {
            viewModelScope.launch {
                // Update the list of addresses
                interactor.getAddresses(cityName!!, guardServiceName!!).fold(
                    onSuccess = { addresses ->
                        if (addresses.count() > 0) {
                            interactor.updateAddresses(addresses)
                        }
                    },
                    onError = {}
                )

                // Prepare the SessionDataHolder instance
                val addresses = interactor.getCachedAddresses().mapNotNull { ipAddress ->
                    try {
                        Address.create(ipAddress, PORT)
                    } catch (ex: IllegalArgumentException) {
                        null
                    }
                }

                sessionDataHolder.setAddresses(addresses)
                sessionDataHolder.setToken(token)

                router.newRootScreen(Screens.passcode())
            }
        } else {
            router.newRootScreen(Screens.login())
        }
    }

}