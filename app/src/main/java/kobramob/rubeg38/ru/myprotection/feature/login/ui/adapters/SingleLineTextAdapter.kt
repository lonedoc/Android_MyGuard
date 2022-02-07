package kobramob.rubeg38.ru.myprotection.feature.login.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.databinding.DropdownItemBinding

fun <T> singleLineTextAdapter(
    context: Context,
    hint: String,
    items: List<T>,
    transform: (T) -> String
): SingleLineTextAdapter<T> {
    return SingleLineTextAdapter(
        context,
        R.layout.dropdown_item,
        hint,
        items.toMutableList(),
        transform
    )
}

class SingleLineTextAdapter<T> (
    context: Context,
    @LayoutRes textViewRes: Int,
    private val hint: String,
    items: List<T>,
    private val transform: (T) -> String
) : ArrayAdapter<T>(context, textViewRes, items) {

    private var selectedItemPosition: Int = -1

    fun setSelectedItemPosition(position: Int) {
        selectedItemPosition = position
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DropdownItemBinding.inflate(layoutInflater, parent, false)

        if (position == 0) {
            binding.valueText.text = hint

            val color = parent.context.getColor(R.color.translucent_white_80)
            binding.valueText.setTextColor(color)

            return binding.root
        }

        getItem(position)?.let { item ->
            binding.valueText.text = transform(item)

            val colorRes = if (position == selectedItemPosition) {
                R.color.green_500
            } else {
                R.color.white
            }

            binding.valueText.setTextColor(parent.context.getColor(colorRes))
        }

        return binding.root
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DropdownItemBinding.inflate(layoutInflater, parent, false)

        if (position == 0) {
            binding.valueText.text = hint

            val color = parent.context.getColor(R.color.translucent_white_80)
            binding.valueText.setTextColor(color)
        } else {
            getItem(position)?.let { item ->
                binding.valueText.text = transform(item)

                val color = parent.context.getColor(R.color.white)
                binding.valueText.setTextColor(color)
            }
        }

        binding.root.setPadding(
            0,
            binding.root.paddingTop,
            binding.root.paddingRight,
            binding.root.paddingBottom
        )

        return binding.root
    }

    override fun getCount(): Int = super.getCount() + 1

    override fun getItem(position: Int): T? {
        if (position == 0) {
            return null
        }

        return super.getItem(position - 1)
    }

}