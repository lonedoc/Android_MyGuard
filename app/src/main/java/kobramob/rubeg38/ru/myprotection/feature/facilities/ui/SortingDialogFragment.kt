package kobramob.rubeg38.ru.myprotection.feature.facilities.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kobramob.rubeg38.ru.myprotection.databinding.FragmentSortingDialogBinding
import kobramob.rubeg38.ru.myprotection.feature.facilities.domain.models.Sorting

class SortingDialogFragment : BottomSheetDialogFragment() {

    companion object {
        const val REQUEST_KEY = "sorting_dialog_request_key"
        const val SORTING_KEY = "sorting_key"

        fun create(sorting: Sorting) = SortingDialogFragment().apply {
            arguments = Bundle().apply {
                putSerializable(SORTING_KEY, sorting)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSortingDialogBinding.inflate(layoutInflater)
        val sorting = requireArguments().getSerializable(SORTING_KEY) ?: Sorting.BY_STATUS

        when (sorting) {
            Sorting.BY_STATUS -> {
                binding.byAddressCheckbox.isChecked = false
                binding.byNameCheckbox.isChecked = false
                binding.byStatusCheckbox.isChecked = true
            }
            Sorting.BY_ADDRESS_ASCENDING -> {
                binding.byAddressCheckbox.state = 0
                binding.byAddressCheckbox.isChecked = true
                binding.byNameCheckbox.isChecked = false
                binding.byStatusCheckbox.isChecked = false
            }
            Sorting.BY_ADDRESS_DESCENDING -> {
                binding.byAddressCheckbox.state = 1
                binding.byAddressCheckbox.isChecked = true
                binding.byNameCheckbox.isChecked = false
                binding.byStatusCheckbox.isChecked = false
            }
            Sorting.BY_NAME_ASCENDING -> {
                binding.byNameCheckbox.state = 0
                binding.byAddressCheckbox.isChecked = false
                binding.byNameCheckbox.isChecked = true
                binding.byStatusCheckbox.isChecked = false
            }
            Sorting.BY_NAME_DESCENDING -> {
                binding.byNameCheckbox.state = 1
                binding.byAddressCheckbox.isChecked = false
                binding.byNameCheckbox.isChecked = true
                binding.byStatusCheckbox.isChecked = false
            }
        }

        binding.byAddressCheckbox.setOnStateChangedListener { isChecked, stateIndex ->
            if (isChecked) {
                binding.byNameCheckbox.isChecked = false
                binding.byStatusCheckbox.isChecked = false

                val newSorting = if (stateIndex == 0) {
                    Sorting.BY_ADDRESS_ASCENDING
                } else {
                    Sorting.BY_ADDRESS_DESCENDING
                }

                postNewSorting(newSorting)
            }
        }

        binding.byNameCheckbox.setOnStateChangedListener { isChecked, stateIndex ->
            if (isChecked) {
                binding.byAddressCheckbox.isChecked = false
                binding.byStatusCheckbox.isChecked = false

                val newSorting = if (stateIndex == 0) {
                    Sorting.BY_NAME_ASCENDING
                } else {
                    Sorting.BY_NAME_DESCENDING
                }

                postNewSorting(newSorting)
            }
        }

        binding.byStatusCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.byAddressCheckbox.isChecked = false
                binding.byNameCheckbox.isChecked = false

                postNewSorting(Sorting.BY_STATUS)
            } else {
                if (!binding.byAddressCheckbox.isChecked && !binding.byNameCheckbox.isChecked) {
                    binding.byStatusCheckbox.isChecked = true
                }
            }
        }

        return binding.root
    }

    private fun postNewSorting(sorting: Sorting) {
        setFragmentResult(
            REQUEST_KEY,
            Bundle().apply {
                putSerializable(SORTING_KEY, sorting)
            }
        )
    }

}