package kobramob.rubeg38.ru.myprotection.feature.facility.di

import com.github.terrakok.cicerone.Router
import kobramob.rubeg38.ru.myprotection.domain.models.Facility
import kobramob.rubeg38.ru.myprotection.feature.facility.data.FacilityManagementApi
import kobramob.rubeg38.ru.myprotection.feature.facility.data.FacilityManagementRepository
import kobramob.rubeg38.ru.myprotection.feature.facility.data.FacilityManagementRepositoryImpl
import kobramob.rubeg38.ru.myprotection.feature.facility.domain.FacilityInteractor
import kobramob.rubeg38.ru.myprotection.feature.facility.ui.FacilityViewModel
import kobramob.rubeg38.ru.myprotection.utils.SessionDataHolder
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val facilityModule = module {
    single<FacilityManagementApi> {
        FacilityManagementApi(get<SessionDataHolder>())
    }

    single<FacilityManagementRepository> {
        FacilityManagementRepositoryImpl(get<FacilityManagementApi>())
    }

    single<FacilityInteractor> {
        FacilityInteractor(get<FacilityManagementRepository>())
    }

    viewModel<FacilityViewModel> { params ->
        FacilityViewModel(
            params.get<Facility>(),
            androidApplication(),
            get<FacilityInteractor>(),
            get<Router>()
        )
    }
}