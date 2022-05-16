package kobramob.rubeg38.ru.myprotection.feature.applications.di

import com.github.terrakok.cicerone.Router
import kobramob.rubeg38.ru.myprotection.feature.applications.data.ApplicationsApi
import kobramob.rubeg38.ru.myprotection.feature.applications.data.ApplicationsRepository
import kobramob.rubeg38.ru.myprotection.feature.applications.data.ApplicationsRepositoryImpl
import kobramob.rubeg38.ru.myprotection.feature.applications.domain.ApplicationsInteractor
import kobramob.rubeg38.ru.myprotection.feature.applications.ui.ApplicationsViewModel
import kobramob.rubeg38.ru.myprotection.utils.SessionDataHolder
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val applicationsModule = module {
    single<ApplicationsApi> {
        ApplicationsApi(get<SessionDataHolder>())
    }

    single<ApplicationsRepository> {
        ApplicationsRepositoryImpl(get<ApplicationsApi>())
    }

    single<ApplicationsInteractor> {
        ApplicationsInteractor(get<ApplicationsRepository>())
    }

    viewModel { params ->
        ApplicationsViewModel(
            androidApplication(),
            params.get<String>(),
            get<ApplicationsInteractor>(),
            get<Router>()
        )
    }
}