package kobramob.rubeg38.ru.myprotection.feature.facility.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.databinding.FragmentFacilityBinding
import kobramob.rubeg38.ru.myprotection.domain.models.Account
import kobramob.rubeg38.ru.myprotection.domain.models.Facility
import kobramob.rubeg38.ru.myprotection.feature.accounts.ui.AccountsFragment
import kobramob.rubeg38.ru.myprotection.feature.accounts.ui.AccountsViewModel
import kobramob.rubeg38.ru.myprotection.feature.events.ui.EventsFragment
import kobramob.rubeg38.ru.myprotection.feature.events.ui.EventsViewModel
import kobramob.rubeg38.ru.myprotection.feature.testmode.ui.TestModeDialogFragment
import kobramob.rubeg38.ru.myprotection.utils.load
import kobramob.rubeg38.ru.myprotection.utils.setThrottledClickListener
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.OutputStream

class FacilityFragment : Fragment(R.layout.fragment_facility) {

    companion object {
        private const val FACILITY_KEY = "facility"

        fun create(facility: Facility) = FacilityFragment().apply {
            arguments = bundleOf(FACILITY_KEY to facility)
        }
    }

    private val viewModel: FacilityViewModel by viewModel {
        parametersOf(requireArguments().getParcelable<Facility>(FACILITY_KEY))
    }

    private fun getPagerAdapter(): FragmentStateAdapter {
        return object : FragmentStateAdapter(childFragmentManager, lifecycle) {
            override fun getItemCount(): Int = 3

            override fun createFragment(position: Int): Fragment = when (position) {
                0 -> getEventsFragment()
                1 -> getAccountsFragment() // TODO
                else -> getAccountsFragment()
            }

            private fun getEventsFragment(): EventsFragment {
                val facility = requireArguments().getParcelable<Facility>(FACILITY_KEY)
                return EventsFragment.create(facility?.id ?: "")
            }

            private fun getAccountsFragment(): AccountsFragment {
                val facility = requireArguments().getParcelable<Facility>(FACILITY_KEY)
                return AccountsFragment.create(facility?.accounts ?: emptyList())
            }
        }
    }

    private val binding: FragmentFacilityBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appBar.setNavigationOnClickListener {
            viewModel.processUiEvent(UiEvent.OnBackButtonClick)
        }

        binding.appBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.editNameItem -> {
                    viewModel.processUiEvent(UiEvent.OnRenameButtonClick)
                    true
                }
                else -> false
            }
        }

        binding.alarmButton.setThrottledClickListener {
            viewModel.processUiEvent(UiEvent.OnAlarmButtonClick)
        }

        binding.armButton.setThrottledClickListener {
            viewModel.processUiEvent(UiEvent.OnArmButtonClick)
        }

        binding.armButton.setOnLongClickListener {
            viewModel.processUiEvent(UiEvent.OnArmButtonLongClick)
            false
        }

        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.adapter = getPagerAdapter()

        binding.bottomNavigationView.menu.getItem(2).isEnabled = false

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.testModeItem) {
                viewModel.processUiEvent(UiEvent.OnTestButtonClick)
                return@setOnItemSelectedListener false
            }

            return@setOnItemSelectedListener when (item.itemId) {
                R.id.eventsItem -> {
                    binding.viewPager.setCurrentItem(0, true)
                    true
                }
                R.id.sensorsItem -> {
                    binding.viewPager.setCurrentItem(1, true)
                    true
                }
                R.id.accountItem -> {
                    binding.viewPager.setCurrentItem(2, true)
                    true
                }
                else -> false
            }
        }

        binding.bottomNavigationView.selectedItemId = R.id.eventsItem

        lifecycle.addObserver(viewModel)

        setFragmentResultListener(RenameDialogFragment.REQUEST_KEY) { _, bundle ->
            val name = bundle.getString(RenameDialogFragment.NAME_KEY) ?: return@setFragmentResultListener
            viewModel.processUiEvent(UiEvent.OnRenameConfirmed(name))
        }

        viewModel.viewState.observe(viewLifecycleOwner, ::render)
        viewModel.singleEvent.observe(viewLifecycleOwner, ::handleSingleEvent)
    }

    private fun render(viewState: ViewState) {
        val facility = viewState.facility

        binding.appBar.title = facility.name
        binding.statusTextView.text = facility.statusDescription
        binding.addressTextView.text = facility.address

        val onlineChannelIconRes = if (facility.isOnlineChannelEnabled)
            R.drawable.online_channel_on_icon
        else
            R.drawable.online_channel_off_icon

        binding.onlineChannelIcon.load(onlineChannelIconRes)

        binding.onlineChannelIcon.isVisible = facility.hasOnlineChannel
        binding.powerSupplyMalfunctionIcon.isVisible = facility.powerSupplyMalfunction
        binding.batteryMalfunctionIcon.isVisible = facility.batteryMalfunction

        val armButtonImageRes = when {
            facility.alarm && facility.isGuarded -> R.drawable.facility_status_alarm_guarded
            facility.alarm && !facility.isGuarded -> R.drawable.facility_status_alarm_not_guarded
            !facility.alarm && facility.isGuarded -> R.drawable.facility_status_guarded
            else -> R.drawable.facility_status_not_guarded
        }

        binding.armButton.load(armButtonImageRes)
    }

    private fun handleSingleEvent(event: SingleEvent) {
        when(event) {
            is SingleEvent.OnRenameDialog -> {
                showRenameDialog(event.currentName)
            }
            is SingleEvent.OnConfirmationDialog -> {
                showConfirmationDialog(
                    event.messageRes,
                    event.positiveButtonTextRes
                ) {
                    viewModel.processUiEvent(event.uiEvent)
                }
            }
            is SingleEvent.OnTestModeDialog -> {
                showTestModeDialog(event.facilityId)
            }
            is SingleEvent.OnError -> {
                showErrorMessage(event.errorMessageRes)
            }
        }
    }

    private fun showRenameDialog(currentName: String) {
        RenameDialogFragment.create(currentName).show(
            requireActivity().supportFragmentManager,
            RenameDialogFragment::class.simpleName
        )
    }

    private fun showConfirmationDialog(
        @StringRes messageRes: Int,
        @StringRes positiveButtonTextRes: Int,
        onProceed: () -> Unit
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.confirmation_dialog_title)
            .setMessage(messageRes)
            .setPositiveButton(positiveButtonTextRes) { _, _ -> onProceed() }
            .setNegativeButton(R.string.confirmation_dialog_negative_button_text) { _, _ -> }
            .create()
            .show()
    }

    private fun showTestModeDialog(facilityId: String) {
        TestModeDialogFragment.create(facilityId).show(
            requireActivity().supportFragmentManager,
            TestModeDialogFragment::class.simpleName
        )
    }

    private fun showErrorMessage(@StringRes errorMessageRes: Int) {
        Toast.makeText(requireContext(), errorMessageRes, Toast.LENGTH_LONG).show()
    }

}