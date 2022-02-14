package kobramob.rubeg38.ru.myprotection

import com.github.terrakok.cicerone.androidx.FragmentScreen
import kobramob.rubeg38.ru.myprotection.domain.models.GuardService
import kobramob.rubeg38.ru.myprotection.feature.login.ui.LoginFragment
import kobramob.rubeg38.ru.myprotection.feature.objects.ui.ObjectsFragment
import kobramob.rubeg38.ru.myprotection.feature.passcode.ui.PasscodeFragment
import kobramob.rubeg38.ru.myprotection.feature.password.ui.PasswordFragment
import kobramob.rubeg38.ru.myprotection.feature.splashscreen.ui.SplashScreenFragment

object Screens {

    fun splashscreen() = FragmentScreen {
        SplashScreenFragment.create()
    }

    fun login() = FragmentScreen {
        LoginFragment.create()
    }

    fun password(guardService: GuardService, phoneNumber: String) = FragmentScreen {
        PasswordFragment.create(guardService, phoneNumber)
    }

    fun passcode() = FragmentScreen {
        PasscodeFragment.create()
    }

    fun objects() = FragmentScreen {
        ObjectsFragment.create()
    }

}