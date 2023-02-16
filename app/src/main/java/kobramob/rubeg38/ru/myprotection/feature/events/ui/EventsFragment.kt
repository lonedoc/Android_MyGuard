package kobramob.rubeg38.ru.myprotection.feature.events.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegate
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.databinding.FragmentEventsBinding
import kobramob.rubeg38.ru.myprotection.utils.loadData
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val SCROLL_DOWN_CODE = 1

class EventsFragment : Fragment(R.layout.fragment_events) {

    companion object {
        private const val FACILITY_ID_KEY = "facilityId"

        fun create(facilityId: String) = EventsFragment().apply {
            arguments = bundleOf(FACILITY_ID_KEY to facilityId)
        }
    }

    private val viewModel: EventsViewModel by viewModel {
        val facilityId = requireArguments().getString(FACILITY_ID_KEY)
        parametersOf(facilityId)
    }

    private val binding: FragmentEventsBinding by viewBinding()
    private val eventsAdapter by lazy { ListDelegationAdapter(eventsAdapterDelegate()) }

    private val onScrollListener by lazy {
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(SCROLL_DOWN_CODE)) {
                    viewModel.processUiEvent(UiEvent.OnEndOfListReached)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayout.setColorSchemeColors(
            requireContext().getColor(R.color.blue_500)
        )

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.processUiEvent(UiEvent.OnEventsRequest)
        }

        binding.eventsRecyclerView.adapter = eventsAdapter

        binding.eventsRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), 0)
        )

        binding.eventsRecyclerView.addOnScrollListener(onScrollListener)

        lifecycle.addObserver(viewModel)

        viewModel.viewState.observe(viewLifecycleOwner, ::render)
        viewModel.singleEvent.observe(viewLifecycleOwner, ::handleSingleEvent)
    }

    private fun render(viewState: ViewState) {
        val state = binding.eventsRecyclerView.layoutManager?.onSaveInstanceState()
        eventsAdapter.loadData(viewState.events)
        binding.eventsRecyclerView.layoutManager?.onRestoreInstanceState(state)

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