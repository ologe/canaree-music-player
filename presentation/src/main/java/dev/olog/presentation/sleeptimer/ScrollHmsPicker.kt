package dev.olog.presentation.sleeptimer

import android.content.Context
import android.graphics.Typeface
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import dev.olog.presentation.R

class ScrollHmsPicker (
        context: Context,
        attrs: AttributeSet
) : LinearLayout(context, attrs) {
    private val pickerHours: NumberPickerView
    private val textHours: TextView
    private val pickerMinutes: NumberPickerView
    private val textMinutes: TextView
    private val pickerSeconds: NumberPickerView
    private val textSeconds: TextView

    private var autoStep: Boolean = false

    var hours: Int
        get() = pickerHours.value
        set(value) = setSafeHours(value)

    var minutes: Int
        get() = pickerMinutes.value
        set(value) = setSafeMinutes(value)

    var seconds: Int
        get() = pickerSeconds.value
        set(value) = setSafeSeconds(value)

    init {
        orientation = LinearLayout.HORIZONTAL
        LayoutInflater.from(context).inflate(R.layout.layout_picker, this)

        val res = resources

        val ta = context.obtainStyledAttributes(attrs, R.styleable.ScrollHmsPicker)

        val colorNormal = ta.getColor(R.styleable.ScrollHmsPicker_shp_normal_color,
                color(android.R.color.darker_gray))
        @ColorInt val colorSelected = ta.getColor(R.styleable.ScrollHmsPicker_shp_selected_color,
                color(android.R.color.holo_red_light))
        val hours = ta.getInteger(R.styleable.ScrollHmsPicker_shp_hours, 0)
        val minutes = ta.getInteger(R.styleable.ScrollHmsPicker_shp_minutes, 0)
        val seconds = ta.getInteger(R.styleable.ScrollHmsPicker_shp_seconds, 0)
        val autoStep = ta.getBoolean(R.styleable.ScrollHmsPicker_shp_auto_step, false)
        ta.recycle()

        pickerHours = findViewById<NumberPickerView>(R.id.pickerHours).apply {
            maxValue = 23
        }
        textHours = findViewById(R.id.textHours)
        pickerMinutes = findViewById<NumberPickerView>(R.id.pickerMinutes).apply {
            maxValue = 59
        }
        textMinutes = findViewById(R.id.textMinutes)
        pickerSeconds = findViewById<NumberPickerView>(R.id.pickerSeconds).apply {
            maxValue = 59
        }
        textSeconds = findViewById(R.id.textSeconds)

        setSafeHours(hours)
        setSafeMinutes(minutes)
        setSafeSeconds(seconds)
        setAutoStep(autoStep)

        arrayOf(pickerHours, pickerMinutes, pickerSeconds).forEach {
            it.setContentTextTypeface(Typeface.SANS_SERIF)
            it.setNormalTextColor(colorNormal)
            it.setSelectedTextColor(colorSelected)
        }
        //
        //----------|
        //          |
        // selected |     |--|   <- label
        //          |     |--|
        //          |         ---->  move label to the bottom of selected item
        // ---------|                padding == (selected size - label size) / 2
        //
        val textMarginTop = ((res.getDimension(R.dimen.text_size_selected_item)
                - res.getDimension(R.dimen.text_size_label)) / 2).toInt()

        arrayOf(textHours, textMinutes, textSeconds).forEach {
            it.setTextColor(colorSelected)
            // align texts to the bottom of the selected text
            it.layoutParams = (it.layoutParams as LinearLayout.LayoutParams).also {
                it.topMargin = textMarginTop
            }
        }
    }

    fun setColorNormal(@ColorRes res: Int) {
        arrayOf(pickerHours, pickerMinutes, pickerSeconds).forEach {
            it.setNormalTextColor(color(res))
        }
    }

    fun setColorSelected(@ColorRes res: Int) {
        val colorInt = color(res)
        arrayOf(pickerHours, pickerMinutes, pickerSeconds).forEach {
            it.setSelectedTextColor(colorInt)
        }
        arrayOf(textHours, textMinutes, textSeconds).forEach {
            it.setTextColor(colorInt)
        }
    }

    fun setAutoStep(newValue: Boolean) {
        if (newValue != autoStep) {
            autoStep = newValue
            if (autoStep) {
                pickerMinutes.setOnValueChangeListenerInScrolling { _, oldVal, newVal ->
                    val hoursVal = pickerHours.value
                    if (oldVal == 59 && newVal == 0 && hoursVal < 24) {
                        pickerHours.smoothScrollToValue(hoursVal + 1)
                    }
                }
                pickerSeconds.setOnValueChangeListenerInScrolling { _, oldVal, newVal ->
                    val minutesVal = pickerMinutes.value
                    if (oldVal == 59 && newVal == 0 && minutesVal < 60) {
                        pickerMinutes.smoothScrollToValue(minutesVal + 1)
                    }
                }
            } else {
                pickerMinutes.setOnValueChangeListenerInScrolling(null)
                pickerSeconds.setOnValueChangeListenerInScrolling(null)
            }
        }
    }

    fun setTime(hours: Int, minutes: Int, seconds: Int, smooth: Boolean){
        if (smooth){
            pickerHours.smoothScrollToValue(hours, false)
            pickerMinutes.smoothScrollToValue(minutes, false)
            pickerSeconds.smoothScrollToValue(seconds, false)
        } else {
            this.hours = hours
            this.minutes = minutes
            this.seconds = seconds
        }
    }

    private fun setSafeHours(hours: Int) {
        if (hours in 0..23) scrollToValue(pickerHours, hours)
    }

    private fun setSafeMinutes(minutes: Int) {
        if (minutes in 0..59) scrollToValue(pickerMinutes, minutes)
    }

    private fun setSafeSeconds(seconds: Int) {
        if (seconds in 0..59) scrollToValue(pickerSeconds, seconds)
    }

    private fun scrollToValue(picker: NumberPickerView, value: Int) {
//        if (animateToValue) {
//            post { picker.smoothScrollToValue(value) }
//        } else {
        picker.value = value
//        }
    }

    override fun onSaveInstanceState(): Parcelable = SavedState(
        super.onSaveInstanceState()!!
    )
            .also { state ->
                state.hours = hours
                state.minutes = minutes
                state.seconds = seconds
            }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            hours = state.hours
            minutes = state.minutes
            seconds = state.seconds
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private class SavedState : View.BaseSavedState {
        var hours: Int = 0
        var minutes: Int = 0
        var seconds: Int = 0

        constructor(superState: Parcelable) : super(superState)

        private constructor(source: Parcel?) : super(source) {
            source?.run {
                hours = source.readInt()
                minutes = source.readInt()
                seconds = source.readInt()
            }
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.run {
                writeInt(hours)
                writeInt(minutes)
                writeInt(seconds)
            }
        }

        private companion object {
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel?): SavedState =
                    SavedState(source)

                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }
    }

    fun View.color(@ColorRes id: Int) = ContextCompat.getColor(this.context, id)
}