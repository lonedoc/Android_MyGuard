package kobramob.rubeg38.ru.myprotection.feature.facilities.ui.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import kobramob.rubeg38.ru.myprotection.R
import kotlin.properties.Delegates

class QuadStateCheckbox(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val titleTextView: TextView
    private val subtitleTextView: TextView
    private val iconImageView: ImageView

    private val title: String
    private val tintColor: Int
    private val subtitles: List<String>
    private val icons: List<Drawable?>

    private var onStateChangedListener: ((Boolean, Int) -> Unit)? = null

    var state: Int = 0
        set(value) {
            require(value in 0..1) {
                "State value must be between 0 and 1"
            }

            field = value
            updateView()
        }

    var isChecked: Boolean by Delegates.observable(false) { _, _, _ ->
        updateView()
    }

    init {
        inflate(context, R.layout.quad_state_checkbox, this)

        titleTextView = findViewById(R.id.titleTextView)
        subtitleTextView = findViewById(R.id.subtitleTextView)
        iconImageView = findViewById(R.id.iconImageView)

        context.obtainStyledAttributes(attrs, R.styleable.QuadStateCheckbox).run {
            try {
                title = getString(R.styleable.QuadStateCheckbox_title) ?: ""
                tintColor = getColor(R.styleable.QuadStateCheckbox_tintColor, context.getColor(R.color.green_500))

                val subtitle1 = getString(R.styleable.QuadStateCheckbox_subtitle1) ?: ""
                val subtitle2 = getString(R.styleable.QuadStateCheckbox_subtitle2) ?: ""
                subtitles = listOf(subtitle1, subtitle2)

                val icon1 = getDrawable(R.styleable.QuadStateCheckbox_icon1)
                val icon2 = getDrawable(R.styleable.QuadStateCheckbox_icon2)
                icons = listOf(icon1, icon2)
            } finally {
                recycle()
            }
        }

        this.setOnClickListener {
            if (!isChecked) {
                isChecked = true
            } else {
                state = (state + 1) % 2
            }

            onStateChangedListener?.invoke(isChecked, state)
        }

        updateView()
    }

    fun setOnStateChangedListener(listener: (Boolean, Int) -> Unit) {
        onStateChangedListener = listener
    }

    fun removeOnStateChangedListener() {
        onStateChangedListener = null
    }

    private fun updateView() {
        titleTextView.text = title
        subtitleTextView.text = subtitles[state]
        iconImageView.setImageDrawable(icons[state])

//        val titleColor = if (isChecked) {
//            tintColor
//        } else {
//            context.getColor(R.color.black)
//        }

        val titleColor = if (isChecked) {
            getColorFromRes(context, R.color.blue_500)
        } else {
            getColorFromTheme(context, R.attr.surfaceForegroundColor)
        }

        titleTextView.setTextColor(titleColor)

        val iconColor = if (isChecked) {
            tintColor
        } else {
            context.getColor(R.color.gray_500)
        }

        iconImageView.setColorFilter(iconColor)
    }

    private fun getColorFromTheme(context: Context, @AttrRes attr: Int): Int =
        TypedValue()
            .also { value -> context.theme.resolveAttribute(attr, value, true) }
            .data

    private fun getColorFromRes(context: Context, @ColorRes resId: Int): Int =
        context.getColor(resId)

}