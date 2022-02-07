package kobramob.rubeg38.ru.myprotection.feature.login.di

import com.github.terrakok.cicerone.Router
import kobramob.rubeg38.ru.myprotection.data.SharedPreferencesHelper
import kobramob.rubeg38.ru.myprotection.feature.login.data.GuardServicesApi
import kobramob.rubeg38.ru.myprotection.feature.login.data.GuardServicesRepository
import kobramob.rubeg38.ru.myprotection.feature.login.data.GuardServicesRepositoryImpl
import kobramob.rubeg38.ru.myprotection.feature.login.domain.LoginFormInteractor
import kobramob.rubeg38.ru.myprotection.feature.login.ui.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val loginModule = module {
    single<GuardServicesApi> {
        GuardServicesApi()
    }

    single<GuardServicesRepository> {
        GuardServicesRepositoryImpl(get<GuardServicesApi>())
    }

    single<LoginFormInteractor> {
        LoginFormInteractor(get<GuardServicesRepository>(), get<SharedPreferencesHelper>())
    }

    viewModel<LoginViewModel> {
        LoginViewModel(get<LoginFormInteractor>(), get<Router>())
    }
}