package kobramob.rubeg38.ru.myprotection.feature.passcode.di

import com.github.terrakok.cicerone.Router
import kobramob.rubeg38.ru.myprotection.data.LoginRepository
import kobramob.rubeg38.ru.myprotection.data.SharedPreferencesHelper
import kobramob.rubeg38.ru.myprotection.feature.passcode.domain.PasscodeInteractor
import kobramob.rubeg38.ru.myprotection.feature.passcode.ui.PasscodeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val passcodeModule = module {
    single<PasscodeInteractor> {
        PasscodeInteractor(
            get<LoginRepository>(),
            get<SharedPreferencesHelper>()
        )
    }

    viewModel<PasscodeViewModel> {
        PasscodeViewModel(
            get<PasscodeInteractor>(),
            get<Router>()
        )
    }
}