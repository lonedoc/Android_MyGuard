package kobramob.rubeg38.ru.myprotection.feature.accounts.di

import com.github.terrakok.cicerone.Router
import kobramob.rubeg38.ru.myprotection.domain.models.Account
import kobramob.rubeg38.ru.myprotection.feature.accounts.ui.AccountsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val accountsModule = module {
    viewModel<AccountsViewModel> { params ->
        AccountsViewModel(
            params.get<List<Account>>(),
            get<Router>()
        )
    }
}