package kobramob.rubeg38.ru.myprotection.feature.facility.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.databinding.FragmentFacilityBinding
import kobramob.rubeg38.ru.myprotection.domain.models.Facility

class FacilityFragment : Fragment(R.layout.fragment_facility) {

    companion object {
        private const val FACILITY_KEY = "facility"

        fun create(facility: Facility) = FacilityFragment().apply {
            arguments = bundleOf(FACILITY_KEY to facility)
        }
    }

    private val binding: FragmentFacilityBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appBar.setNavigationOnClickListener {
            // TODO: Navigate back to the facilities screen
        }

        binding.bottomNavigationView.menu.getItem(2).isEnabled = false

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.eventsItem -> {
                    // TODO: Show events
                    true
                }
                R.id.sensorsItem -> {
                    // TODO: Show sensors
                    true
                }
                R.id.accountItem -> {
                    // TODO: Show the payment form
                    true
                }
                R.id.testModeItem -> {
                    // TODO: Start the test mode
                    false
                }
                else -> false
            }
        }

        Glide
            .with(requireContext())
            .load(R.drawable.facility_status_alarm_guarded)
            .into(binding.armButton)
    }

}