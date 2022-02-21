package kobramob.rubeg38.ru.myprotection.feature.facilities.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.databinding.FragmentFacilitiesBinding
import kobramob.rubeg38.ru.myprotection.utils.loadData
import org.koin.androidx.viewmodel.ext.android.viewModel

class FacilitiesFragment : Fragment(R.layout.fragment_facilities) {

    companion object {
        fun create() = FacilitiesFragment()
    }

    private val viewModel: FacilitiesViewModel by viewModel()
    private val binding: FragmentFacilitiesBinding by viewBinding()

    private val facilitiesAdapter by lazy {
        ListDelegationAdapter(
            facilitiesAdapterDelegate { facility ->
                viewModel.processUiEvent(UiEvent.OnFacilityItemClick(facility) )
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayout.setColorSchemeColors(
            requireContext().getColor(R.color.green_500)
        )

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.processUiEvent(UiEvent.OnFacilitiesRequest)
        }

        binding.facilitiesRecyclerView.adapter = facilitiesAdapter

        binding.facilitiesRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        lifecycle.addObserver(viewModel)

        viewModel.viewState.observe(viewLifecycleOwner, ::render)
        viewModel.singleEvent.observe(viewLifecycleOwner, ::handleSingleEvent)
    }

    private fun render(viewState: ViewState) {
        facilitiesAdapter.loadData(viewState.facilities)

        binding.swipeRefreshLayout.isRefreshing = viewState.isRefresherShown
    }

    private fun handleSingleEvent(event: SingleEvent) {
        when(event) {
            is SingleEvent.OnError -> {
                showErrorMessage(event.errorMessageRes)
            }
        }
    }

    private fun showErrorMessage(@StringRes errorMessageRes: Int) {
        Toast.makeText(requireContext(), errorMessageRes, Toast.LENGTH_LONG).show()
    }

}