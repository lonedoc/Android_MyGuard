package kobramob.rubeg38.ru.myprotection

import com.github.terrakok.cicerone.androidx.FragmentScreen
import kobramob.rubeg38.ru.myprotection.domain.models.Account
import kobramob.rubeg38.ru.myprotection.domain.models.Facility
import kobramob.rubeg38.ru.myprotection.domain.models.GuardService
import kobramob.rubeg38.ru.myprotection.feature.accounts.ui.AccountsFragment
import kobramob.rubeg38.ru.myprotection.feature.accounts.ui.PaymentPageFragment
import kobramob.rubeg38.ru.myprotection.feature.login.ui.LoginFragment
import kobramob.rubeg38.ru.myprotection.feature.facilities.ui.FacilitiesFragment
import kobramob.rubeg38.ru.myprotection.feature.facility.ui.FacilityFragment
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

    fun facilities() = FragmentScreen {
        FacilitiesFragment.create()
    }

    fun facility(facility: Facility) = FragmentScreen {
        FacilityFragment.create(facility)
    }

    fun accounts(accounts: List<Account>) = FragmentScreen {
        AccountsFragment.create(accounts)
    }

    fun paymentPage(account: Account, sum: String) = FragmentScreen {
        PaymentPageFragment.create(account, sum)
    }

}