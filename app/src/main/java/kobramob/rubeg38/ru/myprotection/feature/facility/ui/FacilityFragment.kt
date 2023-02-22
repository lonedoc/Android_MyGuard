package kobramob.rubeg38.ru.myprotection.feature.facility.ui

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.databinding.FragmentFacilityBinding
import kobramob.rubeg38.ru.myprotection.domain.models.Facility
import kobramob.rubeg38.ru.myprotection.feature.accounts.ui.AccountsFragment
import kobramob.rubeg38.ru.myprotection.feature.applications.ui.ApplicationsFragment
import kobramob.rubeg38.ru.myprotection.feature.events.ui.EventsFragment
import kobramob.rubeg38.ru.myprotection.feature.facilities.ui.getColorResByStatus
import kobramob.rubeg38.ru.myprotection.feature.sensors.ui.SensorsFragment
import kobramob.rubeg38.ru.myprotection.feature.sensors.ui.SensorsViewModel
import kobramob.rubeg38.ru.myprotection.feature.testmode.ui.TestModeDialogFragment
import kobramob.rubeg38.ru.myprotection.utils.load
import kobramob.rubeg38.ru.myprotection.utils.setThrottledClickListener
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kobramob.rubeg38.ru.myprotection.feature.sensors.ui.UiEvent as SensorsUiEvent


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

    private val sensorsViewModel: SensorsViewModel by sharedViewModel()
    private val binding: FragmentFacilityBinding by viewBinding()
    private var pagerAdapter: FragmentStateAdapter? = null

    private fun initializePagerAdapter() {
        pagerAdapter = object : FragmentStateAdapter(childFragmentManager, lifecycle) {

            override fun getItemCount(): Int = 4

            override fun createFragment(position: Int): Fragment = when (position) {
                0 -> createEventsFragment()
                1 -> createApplicationFragment()
                2 -> createAccountsFragment()
                3 -> createSensorsFragment()

                else ->{createAccountsFragment()}
            }

            private fun createEventsFragment(): EventsFragment {
                val facility = requireArguments().getParcelable<Facility>(FACILITY_KEY)
                return EventsFragment.create(facility?.id ?: "")
            }

            private fun createSensorsFragment() = SensorsFragment.create()

            private fun createApplicationFragment() = ApplicationsFragment.create(FACILITY_KEY)

            private fun createAccountsFragment(): AccountsFragment {
                val facility = requireArguments().getParcelable<Facility>(FACILITY_KEY)
                return AccountsFragment.create(facility?.accounts ?: emptyList())
            }

        }
    }

    private val array = arrayOf(
        "События",
        "Заявки",
        "Оплата",
        "Датчики"
    )

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
                R.id.testAlarmItem->{
                    viewModel.processUiEvent(UiEvent.OnTestButtonClick)
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
            showProgressDialog()
        }

        binding.armButton.setOnLongClickListener {
            viewModel.processUiEvent(UiEvent.OnArmButtonLongClick)
            false
        }

        initializePagerAdapter()



        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout,binding.viewPager){
            tab,position->
            tab.text = array[position]

        }.attach()

        lifecycle.addObserver(viewModel)

        setFragmentResultListener(RenameDialogFragment.REQUEST_KEY) { _, bundle ->
            val name = bundle.getString(RenameDialogFragment.NAME_KEY) ?: return@setFragmentResultListener
            viewModel.processUiEvent(UiEvent.OnRenameConfirmed(name))
        }

        setFragmentResultListener(CancelAlarmDialogFragment.REQUEST_KEY) { _, bundle ->
            val selectedPasscode = bundle.getString(CancelAlarmDialogFragment.PASSCODE_KEY) ?: return@setFragmentResultListener
            viewModel.processUiEvent(UiEvent.OnCancelAlarmConfirmed(selectedPasscode))
        }

        viewModel.viewState.observe(viewLifecycleOwner, ::render)
        viewModel.singleEvent.observe(viewLifecycleOwner, ::handleSingleEvent)
    }

    private fun render(viewState: ViewState) {
        val params: ConstraintLayout.LayoutParams = binding.tabLayout.layoutParams as ConstraintLayout.LayoutParams
        val facility = viewState.facility

        val onlineChannelIconRes = if (facility.isOnlineChannelEnabled) {
            binding.armButton.visibility = View.VISIBLE
            R.drawable.online_channel_on_icon
        } else {
            params.setMargins(params.leftMargin,params.topMargin-50,params.rightMargin,params.bottomMargin)
            binding.tabLayout.layoutParams = params
            binding.armButton.visibility = View.GONE
            R.drawable.online_channel_off_icon
        }

        val armButtonImageRes = when {
            !facility.alarm && facility.isGuarded -> {
                binding.statusIcon.visibility = View.VISIBLE
                R.drawable.status_guarded_icon
            }
            else -> {
                binding.statusIcon.visibility = View.VISIBLE
                R.drawable.status_not_guarded_icon
            }
        }

        when{
            facility.alarm->{
                binding.statusIcon.visibility = View.INVISIBLE
                binding.statusPulse.visibility = View.VISIBLE
                binding.statusPulse.start()
            }
            else ->{
                binding.statusPulse.visibility = View.GONE
                binding.statusPulse.stop()
                binding.statusIcon.load(armButtonImageRes)
            }
        }


        val alarmButtonColor = AppCompatResources.getColorStateList(
            requireContext(),
            viewState.alarmButtonColorRes
        )
        val armButtonColor = AppCompatResources.getColorStateList(
            requireContext(),
            viewState.armButtonColorRes
        )

        val alarmButtonText = viewState.alarmButtonText?.let { getText(it) }
        val armButtonText = viewState.armButtonText?.let { getText(it) }


        when{
            viewState.isProgressBarShown && binding.determinateBar!!.isShown && !viewState.pendingArmingOrDisarming->{
                hideProgressDialog()
            }
            viewState.pendingArmingOrDisarming && facility.armTime!!>=5->{
                hideProgressDialog()
                if(!timerDialogShow)
                    showTimerDialog(facility.armTime)
            }
            facility.statusDescription=="Под охраной"->{
                hideProgressDialog()
            }
            facility.isApplicationsEnabled->{(binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(1).visibility = View.GONE}
        }

        if(facility.devices.isEmpty())
            (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(3).visibility = View.GONE


        if(!facility.isAlarmButtonEnabled){
            binding.alarmButton.visibility = View.GONE
            params.setMargins(params.leftMargin,170,params.rightMargin,params.bottomMargin)
            binding.tabLayout.layoutParams = params
        }

        if(!facility.isSelfServiceEnabled){
            (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(2).visibility = View.GONE
        }

        val facilityInfo = facility.name + " - " + facility.address
        binding.appBar.setTitle(R.string.detail)
        binding.facilityName.text = facilityInfo
        binding.facilityStatus.text = facility.statusDescription
        binding.facilityStatus.setTextColor(resources.getColor(getColorResByStatus(facility.statusCodes),null))
        binding.onlineChannelIcon.load(onlineChannelIconRes)
        binding.onlineChannelIcon.isVisible = facility.hasOnlineChannel
        binding.powerSupplyMalfunctionIcon.isVisible = facility.powerSupplyMalfunction
        binding.batteryMalfunctionIcon.isVisible = facility.batteryMalfunction
        binding.alarmButton.backgroundTintList = alarmButtonColor
        binding.armButton.backgroundTintList = armButtonColor
        binding.alarmButton.text = alarmButtonText
        binding.armButton.text = armButtonText

        sensorsViewModel.processUiEvent(SensorsUiEvent.OnDevicesListUpdated(facility.devices))
    }

    private fun handleSingleEvent(event: SingleEvent) {
        when(event) {
            is SingleEvent.OnCancelAlarmDialog -> {
                showCancelAlarmDialog(event.passcodes)
            }
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

    private fun showCancelAlarmDialog(passcodes: List<String>) {
        CancelAlarmDialogFragment.create(passcodes).show(
            requireActivity().supportFragmentManager,
            CancelAlarmDialogFragment::class.simpleName
        )
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
        AlertDialog.Builder(requireContext(),R.style.AlertDialogCustom)
            .setTitle(R.string.confirmation_dialog_title)
            .setMessage(messageRes)
            .setPositiveButton(positiveButtonTextRes) { _, _ -> onProceed() }
            .setNegativeButton(R.string.confirmation_dialog_negative_button_text) { _, _ -> hideProgressDialog() }
            .create()
            .show()
    }

    private fun showTestModeDialog(facilityId: String) {
        TestModeDialogFragment.create(facilityId).show(
            requireActivity().supportFragmentManager,
            TestModeDialogFragment::class.simpleName
        )
    }

    private var timerDialogShow:Boolean = false

    private var dialog:Dialog?  = null

    private fun showTimerDialog(armTime:Int){
        dialog = Dialog(requireContext())
        timerDialogShow=true
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setContentView(R.layout.dialog_timer)
        val cancelButton = dialog?.findViewById(R.id.cancelButton) as Button
        val countdownTextView = dialog?.findViewById(R.id.countdownTextView) as TextView
        val title = dialog?.findViewById(R.id.title) as TextView
        title.text = "Время до постановки на охрану"
        object : CountDownTimer(((armTime)*1000).toLong(), 1000) {

            // Callback function, fired on regular interval
            override fun onTick(millisUntilFinished: Long) {
                if(millisUntilFinished<10000)
                {
                    countdownTextView.text = "00 : 0"+millisUntilFinished / 1000
                }
                else
                {
                    countdownTextView.text = "00 : "+millisUntilFinished / 1000
                }

            }

            // Callback function, fired
            // when the time is up
            override fun onFinish() {
                closeTimerDialog()
            }
        }.start()

        cancelButton.setThrottledClickListener {
            viewModel.processUiEvent(UiEvent.OnDisarmArmingClick)
            dialog?.hide()
        }

        dialog?.show()
    }
    private fun closeTimerDialog(){
        timerDialogShow = false
        dialog?.hide()
    }

    private fun showProgressDialog(){
        binding.determinateBar?.visibility = View.VISIBLE
    }
    private fun hideProgressDialog(){
        binding.determinateBar?.visibility = View.GONE
    }

    private fun showErrorMessage(@StringRes errorMessageRes: Int) {
        Toast.makeText(requireContext(), errorMessageRes, Toast.LENGTH_LONG).show()
    }

}