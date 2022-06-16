package kobramob.rubeg38.ru.myprotection.feature.passcode.di

import com.github.terrakok.cicerone.Router
import kobramob.rubeg38.ru.myprotection.data.LoginRepository
import kobramob.rubeg38.ru.myprotection.data.SharedPreferencesHelper
import kobramob.rubeg38.ru.myprotection.feature.passcode.domain.PasscodeInteractor
import kobramob.rubeg38.ru.myprotection.feature.passcode.ui.PasscodeViewModel
import kobramob.rubeg38.ru.myprotection.utils.BiometricUtil
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val passcodeModule = module {
    single<BiometricUtil> {
        BiometricUtil(androidContext())
    }

    single<PasscodeInteractor> {
        PasscodeInteractor(
            get<LoginRepository>(),
            get<SharedPreferencesHelper>(),
            get<BiometricUtil>()
        )
    }

    viewModel<PasscodeViewModel> {
        PasscodeViewModel(
            get<PasscodeInteractor>(),
            get<Router>()
        )
    }
}