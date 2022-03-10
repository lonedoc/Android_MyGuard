package kobramob.rubeg38.ru.myprotection.feature.facilities.ui

import android.graphics.drawable.InsetDrawable
import androidx.appcompat.content.res.AppCompatResources
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.databinding.FacilitiesItemBinding
import kobramob.rubeg38.ru.myprotection.domain.models.Facility
import kobramob.rubeg38.ru.myprotection.domain.models.StatusCode
import kobramob.rubeg38.ru.myprotection.utils.setThrottledClickListener

fun facilitiesAdapterDelegate(onClick: (Facility) -> Unit) =
    adapterDelegateViewBinding<Facility, Facility, FacilitiesItemBinding>(
        viewBinding = { layoutInflater, parent ->
            FacilitiesItemBinding.inflate(layoutInflater, parent, false)
        }
    ) {
        binding.root.setThrottledClickListener { onClick(item) }

        bind {
            val backgroundColor = context.getColor(getColorResByStatus(item.statusCodes))
            val iconDrawableRes = getIconResByStatus(item.statusCodes)
            val iconDrawable = AppCompatResources.getDrawable(context, iconDrawableRes)
            val drawable = InsetDrawable(iconDrawable, 32)

            binding.iconImageView.setImageDrawable(drawable)
            binding.iconImageView.setBackgroundColor(backgroundColor)

            binding.titleTextView.text = item.name
            binding.addressTextView.text = item.address
            binding.statusTextView.text = item.statusDescription
        }
    }

fun getColorResByStatus(statusCodes: List<StatusCode>) = when {
    statusCodes.contains(StatusCode.ALARM) -> R.color.deep_orange_500
    statusCodes.contains(StatusCode.MALFUNCTION) -> R.color.brown_500
    statusCodes.contains(StatusCode.NOT_GUARDED) -> R.color.green_500
    statusCodes.contains(StatusCode.GUARDED) -> R.color.blue_500
    else -> R.color.gray_500
}

fun getIconResByStatus(statusCodes: List<StatusCode>) = when {
    statusCodes.contains(StatusCode.NOT_GUARDED) -> R.drawable.status_not_guarded_icon
    statusCodes.contains(StatusCode.PERIMETER_ONLY) -> R.drawable.status_perimeter_only_icon
    statusCodes.contains(StatusCode.GUARDED) -> R.drawable.status_guarded_icon
    statusCodes.contains(StatusCode.ALARM) -> R.drawable.status_alarm_icon
    statusCodes.contains(StatusCode.MALFUNCTION) -> R.drawable.status_malfunction_icon
    else -> R.drawable.status_unknown_icon
}