package kobramob.rubeg38.ru.myprotection.feature.passcode.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.*
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.databinding.FragmentPasscodeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PasscodeFragment : Fragment(R.layout.fragment_passcode) {

    companion object {
        fun create() = PasscodeFragment()
    }

    private val viewModel: PasscodeViewModel by viewModel()
    private val binding: FragmentPasscodeBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setKeyboardListeners()

        binding.appBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.exitItem -> {
                    viewModel.processUiEvent(UiEvent.OnExitButtonClick)
                    true
                }
                R.id.callItem -> {
                    viewModel.processUiEvent(UiEvent.OnCallButtonClick)
                    true
                }
                else -> false
            }
        }

        binding.forgotPasscodeButton.setOnClickListener {
            viewModel.processUiEvent(UiEvent.OnForgotCodeButtonClick)
        }

        viewModel.viewState.observe(viewLifecycleOwner, ::render)
        viewModel.singleEvent.observe(viewLifecycleOwner, ::handleSingleEvent)
    }

    private fun render(viewState: ViewState) {
        binding.hintTextView.text = getString(viewState.hintTextRes)

        setIndicatorValue(viewState.indicatorValue)

        if (viewState.isAppBarMenuVisible && binding.appBar.menu.isEmpty()) {
            binding.appBar.inflateMenu(R.menu.passcode_appbar_menu)
        }

        binding.forgotPasscodeButton.isVisible = viewState.isForgotCodeButtonVisible
        binding.versionTextView.text = viewState.version
    }

    private fun handleSingleEvent(event: SingleEvent) {
        when (event) {
            is SingleEvent.OnError -> {
                showErrorMessage(event.errorMessageRes)
            }
            is SingleEvent.OnCall -> {
                val intent = Intent(Intent.ACTION_DIAL, event.uri)
                startActivity(intent)
            }
        }
    }

    private fun setKeyboardListeners() {
        binding.keyboardView.root.children.forEach { child ->
            (child.tag as? String)?.let { tag ->
                if (tag.count() == 1) {
                    child.setOnClickListener {
                        viewModel.processUiEvent(UiEvent.OnCharacterAdded(tag))
                    }
                }
            }
        }

        binding.keyboardView.backspaceButton.setOnClickListener {
            viewModel.processUiEvent(UiEvent.OnCharacterRemoved)
        }
    }

    private fun setIndicatorValue(value: Int) {
        val indicatorItems = listOf(
            binding.indicatorView1,
            binding.indicatorView2,
            binding.indicatorView3,
            binding.indicatorView4
        )

        indicatorItems.forEachIndexed { index, item ->
            val drawableRes = if (index < value) {
                R.drawable.solid_shield_icon
            } else {
                R.drawable.outlined_shield_icon
            }

            item.setImageResource(drawableRes)
        }
    }

    private fun showErrorMessage(@StringRes errorMessageRes: Int) {
        Toast.makeText(requireContext(), errorMessageRes, Toast.LENGTH_LONG).show()
    }

}