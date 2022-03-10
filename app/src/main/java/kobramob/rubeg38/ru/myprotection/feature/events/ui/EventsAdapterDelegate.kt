package kobramob.rubeg38.ru.myprotection.feature.events.ui

import android.annotation.SuppressLint
import android.graphics.drawable.InsetDrawable
import androidx.appcompat.content.res.AppCompatResources
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.databinding.EventsItemBinding
import kobramob.rubeg38.ru.myprotection.feature.events.domain.models.Event
import java.text.SimpleDateFormat
import java.util.*

fun eventsAdapterDelegate() =
    adapterDelegateViewBinding<Event, Event, EventsItemBinding>(
        viewBinding = { layoutInflater, parent ->
            EventsItemBinding.inflate(layoutInflater, parent, false)
        }
    ) {
        bind {
            val backgroundColor = context.getColor(getColorResByType(item.type))
            val iconDrawableRes = getIconResByType(item.type)
            val iconDrawable = AppCompatResources.getDrawable(context, iconDrawableRes)
            val drawable = InsetDrawable(iconDrawable, 32)

            binding.iconImageView.setImageDrawable(drawable)
            binding.iconImageView.setBackgroundColor(backgroundColor)

            binding.descriptionTextView.text = item.description
            binding.zoneTextView.text = item.zone
            binding.timestampTextView.text = getTimestampStr(item.timestamp)
        }
    }

fun getColorResByType(type: Int) = when(type) {
    1, 2, 3, 4, 77, 79, 81, 85 -> R.color.deep_orange_500
    5, 33 -> R.color.brown_500
    10, 68, 69 -> R.color.green_500
    6, 8, 9, 66, 67 -> R.color.blue_500
    else -> R.color.gray_500
}

fun getIconResByType(type: Int) = when(type) {
    1, 2, 3, 77, 79, 85 -> R.drawable.status_alarm_icon
    4 -> R.drawable.fire_alarm_icon
    5 -> R.drawable.status_malfunction_icon
    6, 8, 9, 66, 67 -> R.drawable.status_guarded_icon
    10, 68, 69 -> R.drawable.status_not_guarded_icon
    33, 81 -> R.drawable.battery_icon
    else -> R.drawable.status_unknown_icon
}

@SuppressLint("SimpleDateFormat")
fun getTimestampStr(timestamp: Date): String =
    SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(timestamp)