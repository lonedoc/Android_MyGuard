package kobramob.rubeg38.ru.myprotection.di

import android.content.Context
import android.content.SharedPreferences
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import kobramob.rubeg38.ru.myprotection.data.*
import kobramob.rubeg38.ru.myprotection.domain.LoginInteractor
import kobramob.rubeg38.ru.myprotection.utils.SessionDataHolder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val SHARED_PREFERENCES_ID = "myprotection.DataStorage"

val appModule = module {
    single<Cicerone<Router>> {
        Cicerone.create()
    }

    single<NavigatorHolder> {
        get<Cicerone<Router>>().getNavigatorHolder()
    }

    single<Router> {
        get<Cicerone<Router>>().router
    }

    single<SharedPreferences> {
        androidContext().getSharedPreferences(SHARED_PREFERENCES_ID, Context.MODE_PRIVATE)
    }

    single<SharedPreferencesHelper> {
        SharedPreferencesHelperImpl(get<SharedPreferences>())
    }

    single<SessionDataHolder> {
        SessionDataHolder()
    }

    single<LoginApi> {
        LoginApi(get<SessionDataHolder>())
    }

    single<LoginRepository> {
        LoginRepositoryImpl(get<LoginApi>())
    }

    single<LoginInteractor> {
        LoginInteractor(get<LoginRepository>())
    }
}