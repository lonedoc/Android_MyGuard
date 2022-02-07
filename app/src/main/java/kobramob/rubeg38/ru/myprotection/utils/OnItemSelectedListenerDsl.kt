package kobramob.rubeg38.ru.myprotection.utils

import android.view.View
import android.widget.AdapterView

fun onItemSelectedListener(onItemSelected: (Int) -> Unit) =
    object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, l: Long) {
            onItemSelected(position)
        }

        override fun onNothingSelected(p0: AdapterView<*>?) { }
    }