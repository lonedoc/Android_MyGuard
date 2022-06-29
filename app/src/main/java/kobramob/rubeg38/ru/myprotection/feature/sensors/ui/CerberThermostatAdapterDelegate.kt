package kobramob.rubeg38.ru.myprotection.feature.sensors.ui

import android.content.Context
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.databinding.CerberThermostatItemBinding
import kobramob.rubeg38.ru.myprotection.domain.models.CerberThermostat
import kobramob.rubeg38.ru.myprotection.domain.models.Device
import kobramob.rubeg38.ru.myprotection.utils.load

fun cerberThermostatAdapterDelegate() =
    adapterDelegateViewBinding<CerberThermostat, Device, CerberThermostatItemBinding>(
        viewBinding = { layoutInflater, parent ->
            CerberThermostatItemBinding.inflate(layoutInflater, parent, false)
        }
    ) {
        bind {
            val context = binding.root.context
            val onlineIconDrawableRes = getOnlineIconDrawableRes(item.isOnline)
            val onlineIconBackgroundColor = getOnlineIconColor(context, item.isOnline)
            val temperatureText = getTemperatureText(context, item.temperature)

            binding.titleTextView.text = item.deviceDescription
            binding.onlineIcon.load(onlineIconDrawableRes)
            binding.onlineIcon.setBackgroundColor(onlineIconBackgroundColor)
            binding.temperatureTextView.text = temperatureText
        }
    }

private fun getOnlineIconDrawableRes(isOnline: Boolean) =
    if (isOnline) {
        R.drawable.online_channel_on_icon
    } else {
        R.drawable.online_channel_off_icon
    }

private fun getOnlineIconColor(context: Context, isOnline: Boolean) =
    context.getColor(getOnlineIconColorRes(isOnline))

private fun getOnlineIconColorRes(isOnline: Boolean) =
    if (isOnline) {
        R.color.blue_500
    } else {
        R.color.deep_orange_500
    }

private fun getTemperatureText(context: Context, temperature: Double) =
    context.getString(R.string.temperature_text, temperature)
