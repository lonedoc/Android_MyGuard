package kobramob.rubeg38.ru.myprotection.feature.applications.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.databinding.DropdownItemLightBinding
import kobramob.rubeg38.ru.myprotection.utils.loadData

fun textAdapter(context: Context, items: List<String>) = TextAdapter(
    context,
    R.layout.dropdown_item_light,
    items.toMutableList()
)

class TextAdapter(
    context: Context,
    @LayoutRes textViewRes: Int,
    items: List<String>
) : ArrayAdapter<String>(context, textViewRes, items) {

    private var selectedItemPosition: Int = -1

    fun setSelectedItemPosition(position: Int) {
        selectedItemPosition = position
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DropdownItemLightBinding.inflate(layoutInflater, parent, false)

        getItem(position)?.let { item ->
            binding.valueText.text = item

            val color = if (position == selectedItemPosition) {
                getColorFromRes(parent.context, R.color.green_500)
            } else {
                getColorFromTheme(parent.context, R.attr.surfaceForegroundColor)
            }

            binding.valueText.setTextColor(color)
        }

        return binding.root
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DropdownItemLightBinding.inflate(layoutInflater, parent, false)

        getItem(position)?.let { item ->
            binding.valueText.text = item

            val color = if (position == selectedItemPosition) {
                getColorFromRes(parent.context, R.color.green_500)
            } else {
                getColorFromTheme(parent.context, R.attr.surfaceForegroundColor)
            }

            binding.valueText.setTextColor(color)
        }

        binding.root.setPadding(
            binding.root.paddingLeft,
            binding.root.paddingTop,
            binding.root.paddingRight,
            binding.root.paddingBottom
        )

        return binding.root
    }

    private fun getColorFromTheme(context: Context, @AttrRes attr: Int): Int =
        TypedValue()
            .also { value -> context.theme.resolveAttribute(attr, value, true) }
            .data

    private fun getColorFromRes(context: Context, @ColorRes resId: Int): Int =
        context.getColor(resId)

}