package kobramob.rubeg38.ru.myprotection

import android.app.Application
import kobramob.rubeg38.ru.myprotection.di.appModule
import kobramob.rubeg38.ru.myprotection.feature.accounts.di.accountsModule
import kobramob.rubeg38.ru.myprotection.feature.applications.di.applicationsModule
import kobramob.rubeg38.ru.myprotection.feature.events.di.eventsModule
import kobramob.rubeg38.ru.myprotection.feature.facilities.di.facilitiesModule
import kobramob.rubeg38.ru.myprotection.feature.facility.di.facilityModule
import kobramob.rubeg38.ru.myprotection.feature.login.di.loginModule
import kobramob.rubeg38.ru.myprotection.feature.passcode.di.passcodeModule
import kobramob.rubeg38.ru.myprotection.feature.password.di.passwordModule
import kobramob.rubeg38.ru.myprotection.feature.sensors.di.sensorsModule
import kobramob.rubeg38.ru.myprotection.feature.splashscreen.di.splashScreenModule
import kobramob.rubeg38.ru.myprotection.feature.testmode.di.testModeModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            modules(
                appModule,
                loginModule,
                passwordModule,
                passcodeModule,
                splashScreenModule,
                facilitiesModule,
                facilityModule,
                eventsModule,
                sensorsModule,
                accountsModule,
                testModeModule,
                applicationsModule
            )
        }
    }

}