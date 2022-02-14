package kobramob.rubeg38.ru.myprotection

import android.app.Application
import kobramob.rubeg38.ru.myprotection.di.appModule
import kobramob.rubeg38.ru.myprotection.feature.login.di.loginModule
import kobramob.rubeg38.ru.myprotection.feature.passcode.di.passcodeModule
import kobramob.rubeg38.ru.myprotection.feature.password.di.passwordModule
import kobramob.rubeg38.ru.myprotection.feature.splashscreen.di.splashScreenModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            modules(appModule, loginModule, passwordModule, passcodeModule, splashScreenModule)
        }
    }

}