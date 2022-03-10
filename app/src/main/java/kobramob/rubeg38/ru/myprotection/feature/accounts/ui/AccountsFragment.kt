package kobramob.rubeg38.ru.myprotection.feature.accounts.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.databinding.FragmentAccountsBinding
import kobramob.rubeg38.ru.myprotection.domain.models.Account
import kobramob.rubeg38.ru.myprotection.utils.loadData
import kobramob.rubeg38.ru.myprotection.utils.textChangedListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AccountsFragment : Fragment(R.layout.fragment_accounts) {

    companion object {
        private const val ACCOUNTS_KEY = "accounts"

        fun create(accounts: List<Account>) = AccountsFragment().apply {
            arguments = Bundle().apply { putParcelableArrayList(ACCOUNTS_KEY, ArrayList(accounts)) }
        }
    }

    private val viewModel: AccountsViewModel by viewModel {
        val accounts = requireArguments().getParcelableArrayList<Account>(ACCOUNTS_KEY)
        parametersOf(accounts)
    }

    private val binding: FragmentAccountsBinding by viewBinding()

    private val adapter: AccountsAdapter by lazy {
        AccountsAdapter(requireContext(), mutableListOf())
    }

    private val sumChangedListener = textChangedListener { sumText ->
        viewModel.processUiEvent(UiEvent.OnSumChange(sumText))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.accountsAutoComplete.setAdapter(adapter)

        binding.accountsAutoComplete.setOnItemClickListener { _, _, position, _ ->
            adapter.getItem(position)?.let { account ->
                binding.accountsAutoComplete.setAdapter(null)
                binding.accountsAutoComplete.setText(getTextRepresentation(account))
                binding.accountsAutoComplete.setAdapter(adapter)
                viewModel.processUiEvent(UiEvent.OnAccountSelect(account))
            }
        }

        binding.sumEditText.addTextChangedListener(sumChangedListener)

        binding.payButton.setOnClickListener {
            viewModel.processUiEvent(UiEvent.OnPayButtonClick)
        }

        viewModel.viewState.observe(viewLifecycleOwner, ::render)
    }

    private fun render(viewState: ViewState) {
        adapter.loadData(viewState.accounts)

        updateSum(viewState.sum)

        val errorMessage = viewState.invalidSumMessageRes?.let { errorMessageRes ->
            getString(errorMessageRes)
        }

        binding.sumTextInputLayout.error = errorMessage

        binding.payButton.isEnabled = viewState.isPayButtonEnabled
    }

    private fun getTextRepresentation(account: Account): String {
        val guardServicePart = if (account.guardServiceName == null)
            ""
        else
            " - ${account.guardServiceName}"

        return "${account.id}$guardServicePart"
    }

    private fun updateSum(sum: String) {
        binding.sumEditText.removeTextChangedListener(sumChangedListener)
        binding.sumEditText.setText(sum)
        binding.sumEditText.setSelection(sum.length)
        binding.sumEditText.addTextChangedListener(sumChangedListener)
    }

}