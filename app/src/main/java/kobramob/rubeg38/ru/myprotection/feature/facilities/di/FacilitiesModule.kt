package kobramob.rubeg38.ru.myprotection.feature.facilities.di

import com.github.terrakok.cicerone.Router
import kobramob.rubeg38.ru.myprotection.feature.facilities.data.FacilitiesApi
import kobramob.rubeg38.ru.myprotection.feature.facilities.data.FacilitiesRepository
import kobramob.rubeg38.ru.myprotection.feature.facilities.data.FacilitiesRepositoryImpl
import kobramob.rubeg38.ru.myprotection.feature.facilities.domain.FacilitiesInteractor
import kobramob.rubeg38.ru.myprotection.feature.facilities.ui.FacilitiesViewModel
import kobramob.rubeg38.ru.myprotection.utils.SessionDataHolder
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val facilitiesModule = module {
    single<FacilitiesApi> {
        FacilitiesApi(get<SessionDataHolder>())
    }

    single<FacilitiesRepository> {
        FacilitiesRepositoryImpl(get<FacilitiesApi>())
    }

    single<FacilitiesInteractor> {
        FacilitiesInteractor(get<FacilitiesRepository>())
    }

    viewModel<FacilitiesViewModel> {
        FacilitiesViewModel(
            get<FacilitiesInteractor>(),
            get<Router>()
        )
    }
}