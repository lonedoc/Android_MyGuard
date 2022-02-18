package kobramob.rubeg38.ru.myprotection.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.hannesdorfmann.adapterdelegates4.AbsDelegationAdapter
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

fun <T> AbsDelegationAdapter<List<T>>.loadData(data: List<T>) {
    items = data
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