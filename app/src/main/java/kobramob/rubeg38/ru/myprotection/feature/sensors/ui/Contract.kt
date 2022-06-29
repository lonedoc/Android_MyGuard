package kobramob.rubeg38.ru.myprotection.feature.sensors.ui

import kobramob.rubeg38.ru.myprotection.base.Event
import kobramob.rubeg38.ru.myprotection.domain.models.Device

data class ViewState(
    val devices: List<Device>
)

sealed class UiEvent : Event {
    data class OnDevicesListUpdated(val devices: List<Device>) : UiEvent()
}

sealed class DataEvent : Event {

}