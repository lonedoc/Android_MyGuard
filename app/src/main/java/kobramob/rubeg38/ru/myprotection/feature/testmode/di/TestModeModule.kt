package kobramob.rubeg38.ru.myprotection.feature.testmode.di

import kobramob.rubeg38.ru.myprotection.feature.testmode.data.TestModeApi
import kobramob.rubeg38.ru.myprotection.feature.testmode.data.TestModeRepository
import kobramob.rubeg38.ru.myprotection.feature.testmode.data.TestModeRepositoryImpl
import kobramob.rubeg38.ru.myprotection.feature.testmode.domain.TestModeInteractor
import kobramob.rubeg38.ru.myprotection.feature.testmode.ui.TestModeViewModel
import kobramob.rubeg38.ru.myprotection.utils.SessionDataHolder
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val testModeModule = module {
    single<TestModeApi> {
        TestModeApi(get<SessionDataHolder>())
    }

    single<TestModeRepository> {
        TestModeRepositoryImpl(get<TestModeApi>())
    }

    single<TestModeInteractor> {
        TestModeInteractor(get<TestModeRepository>())
    }

    viewModel<TestModeViewModel> { params ->
        TestModeViewModel(params.get<String>(), get<TestModeInteractor>())
    }
}