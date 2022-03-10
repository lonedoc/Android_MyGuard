package kobramob.rubeg38.ru.myprotection.feature.events.di

import androidx.room.Room
import kobramob.rubeg38.ru.myprotection.feature.events.data.*
import kobramob.rubeg38.ru.myprotection.feature.events.domain.EventsInteractor
import kobramob.rubeg38.ru.myprotection.feature.events.ui.EventsViewModel
import kobramob.rubeg38.ru.myprotection.utils.SessionDataHolder
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private const val DATABASE_NAME = "APP_DATABASE"

val eventsModule = module {
    single<EventsApi> {
        EventsApi(get<SessionDataHolder>())
    }

    single<AppDatabase> {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    factory<EventDao> {
        get<AppDatabase>().eventDao()
    }

    single<EventsRepository> {
        EventsRepositoryImpl(get<EventsApi>(), get<EventDao>())
    }

    single<EventsInteractor> {
        EventsInteractor(get<EventsRepository>())
    }

    viewModel { params ->
        EventsViewModel(params.get<String>(), get<EventsInteractor>())
    }
}