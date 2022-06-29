package kobramob.rubeg38.ru.myprotection.feature.sensors.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.databinding.FragmentSensorsBinding
import kobramob.rubeg38.ru.myprotection.domain.models.Device
import kobramob.rubeg38.ru.myprotection.utils.loadData
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SensorsFragment : Fragment(R.layout.fragment_sensors) {

    companion object {
        fun create() = SensorsFragment()
    }

    private val viewModel: SensorsViewModel by sharedViewModel()
    private val binding: FragmentSensorsBinding by viewBinding()

    private val devicesAdapter by lazy {
        ListDelegationAdapter(
            cerberThermostatAdapterDelegate(),
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sensorsRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = devicesAdapter
        }

        viewModel.viewState.observe(viewLifecycleOwner, ::render)
    }

    private fun render(viewState: ViewState) {
        devicesAdapter.loadData(viewState.devices)
    }

}