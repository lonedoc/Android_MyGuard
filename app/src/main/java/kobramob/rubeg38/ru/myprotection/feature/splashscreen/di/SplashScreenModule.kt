package kobramob.rubeg38.ru.myprotection.feature.splashscreen.di

import com.github.terrakok.cicerone.Router
import kobramob.rubeg38.ru.myprotection.data.SharedPreferencesHelper
import kobramob.rubeg38.ru.myprotection.feature.splashscreen.data.AddressesApi
import kobramob.rubeg38.ru.myprotection.feature.splashscreen.data.AddressesRepository
import kobramob.rubeg38.ru.myprotection.feature.splashscreen.data.AddressesRepositoryImpl
import kobramob.rubeg38.ru.myprotection.feature.splashscreen.domain.SplashScreenInteractor
import kobramob.rubeg38.ru.myprotection.feature.splashscreen.ui.SplashScreenViewModel
import kobramob.rubeg38.ru.myprotection.utils.SessionDataHolder
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val splashScreenModule = module {
    single<AddressesApi> {
        AddressesApi()
    }

    single<AddressesRepository> {
        AddressesRepositoryImpl(
            get<AddressesApi>(),
            get<SharedPreferencesHelper>()
        )
    }

    single<SplashScreenInteractor> {
        SplashScreenInteractor(
            get<AddressesRepository>(),
            get<SharedPreferencesHelper>()
        )
    }

    viewModel<SplashScreenViewModel> {
        SplashScreenViewModel(
            get<SplashScreenInteractor>(),
            get<SessionDataHolder>(),
            get<Router>()
        )
    }
}