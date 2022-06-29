package kobramob.rubeg38.ru.myprotection.feature.sensors.ui

import kobramob.rubeg38.ru.myprotection.base.BaseViewModel
import kobramob.rubeg38.ru.myprotection.base.Event

class SensorsViewModel : BaseViewModel<ViewState>() {

    override fun initialViewState() = ViewState(
        devices = emptyList()
    )

    override suspend fun reduce(event: Event, previousState: ViewState): ViewState? {
        when (event) {
            is UiEvent.OnDevicesListUpdated -> {
                return previousState.copy(devices = event.devices)
            }
            else -> return null
        }
    }

}