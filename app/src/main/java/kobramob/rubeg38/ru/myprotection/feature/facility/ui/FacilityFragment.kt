package kobramob.rubeg38.ru.myprotection.feature.facility.ui

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import kobramob.rubeg38.ru.myprotection.domain.models.Facility

class FacilityFragment : Fragment() {

    companion object {
        private const val FACILITY_KEY = "facility"

        fun create(facility: Facility) = FacilityFragment().apply {
            arguments = bundleOf(FACILITY_KEY to facility)
        }
    }

}