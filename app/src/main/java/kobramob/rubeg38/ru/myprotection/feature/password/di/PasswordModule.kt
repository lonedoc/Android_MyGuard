package kobramob.rubeg38.ru.myprotection.feature.password.di

import com.github.terrakok.cicerone.Router
import kobramob.rubeg38.ru.myprotection.data.SharedPreferencesHelper
import kobramob.rubeg38.ru.myprotection.domain.LoginInteractor
import kobramob.rubeg38.ru.myprotection.domain.models.GuardService
import kobramob.rubeg38.ru.myprotection.feature.password.data.PasswordApi
import kobramob.rubeg38.ru.myprotection.feature.password.data.PasswordRepository
import kobramob.rubeg38.ru.myprotection.feature.password.data.PasswordRepositoryImpl
import kobramob.rubeg38.ru.myprotection.feature.password.domain.PasswordInteractor
import kobramob.rubeg38.ru.myprotection.feature.password.ui.PasswordViewModel
import kobramob.rubeg38.ru.myprotection.utils.SessionDataHolder
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val passwordModule = module {
    single<PasswordApi> {
        PasswordApi(get<SessionDataHolder>())
    }

    single<PasswordRepository> {
        PasswordRepositoryImpl(get<PasswordApi>())
    }

    single<PasswordInteractor> {
        PasswordInteractor(
            get<SessionDataHolder>(),
            get<PasswordRepository>(),
            get<SharedPreferencesHelper>()
        )
    }

    viewModel<PasswordViewModel> { params ->
        PasswordViewModel(
            guardService = params.get<GuardService>(),
            phoneNumber = params.get<String>(),
            passwordInteractor = get<PasswordInteractor>(),
            loginInteractor = get<LoginInteractor>(),
            router = get<Router>()
        )
    }
}