package kobramob.rubeg38.ru.myprotection.utils

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AbsDelegationAdapter
import kobramob.rubeg38.ru.myprotection.R
import java.util.*

private const val DEFAULT_THROTTLE_DELAY = 300L

fun View.setThrottledClickListener(delay: Long = DEFAULT_THROTTLE_DELAY, onClick: (View) -> Unit) {
    setOnClickListener {
        throttle(delay) {
            onClick(it)
        }
    }
}

private var lastClickTimestamp = 0L
fun throttle(delay: Long = DEFAULT_THROTTLE_DELAY, action: () -> Unit): Boolean {
    val currentTimestamp = System.currentTimeMillis()
    val delta = currentTimestamp - lastClickTimestamp
    if (delta !in 0L..delay) {
        lastClickTimestamp = currentTimestamp
        action()
        return true
    }
    return false
}

fun EditText.setDebouncingTextListener(
    debouncePeriod: Long = 300,
    onTextChange: (String) -> Unit
) {
    addTextChangedListener(object : TextWatcher {
        private var timer = Timer()

        override fun onTextChanged(newText: CharSequence?, p1: Int, p2: Int, p3: Int) {
            timer.cancel()
            timer = Timer()
            timer.schedule(
                object : TimerTask() {
                    override fun run() {
                        post {
                            onTextChange(newText.toString())
                        }
                    }
                },
                debouncePeriod
            )
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(p0: Editable?) {}
    })
}


fun EditText.setTextChangedListener(onTextChange: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun onTextChanged(newText: CharSequence?, p1: Int, p2: Int, p3: Int) {
            onTextChange(newText.toString())
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(p0: Editable?) {}
    })
}

fun textChangedListener(onTextChange: (String) -> Unit) = object : TextWatcher {
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(newText: Editable?) {
        onTextChange(newText.toString())
    }
}

@SuppressLint("NotifyDataSetChanged")
fun <T> AbsDelegationAdapter<List<T>>.loadData(data: List<T>) {
    items = data
    notifyDataSetChanged()
}

fun <T> ArrayAdapter<T>.loadData(data: List<T>) {
    clear()

    data.forEachIndexed { index, item ->
        insert(item, index)
    }

    notifyDataSetChanged()
}

fun Timer.schedule(delay: Long, period: Long, task: () -> Unit) {
    schedule(
        object : TimerTask() {
            override fun run() {
                task()
            }
        },
        delay,
        period
    )
}

fun ImageView.load(@DrawableRes drawableRes: Int) {
    Glide
        .with(context)
        .load(drawableRes)
        .into(this)
}