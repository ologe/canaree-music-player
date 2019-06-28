package dev.olog.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import dev.olog.presentation.R

class CustomSeekBar(
    context: Context,
    attrs: AttributeSet

) : AppCompatSeekBar(context, attrs) {

    private var isTouched = false

    private var listener: OnSeekBarChangeListener? = null

    init {
        // TODO needed? not already declared in styles?
        progressDrawable = ContextCompat.getDrawable(context, R.drawable.seek_bar_progress)
    }

    fun setListener(
        onProgressChanged: (Int) -> Unit,
        onStartTouch: (Int) -> Unit,
        onStopTouch: (Int) -> Unit
    ) {

        listener = object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                onProgressChanged(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                isTouched = true
                onStartTouch(progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                isTouched = false
                onStopTouch(progress)
            }
        }

        setOnSeekBarChangeListener(null) // clear old listener
        setOnSeekBarChangeListener(listener)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setOnSeekBarChangeListener(listener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setOnSeekBarChangeListener(null)
    }

    override fun setProgress(progress: Int) {
        if (!isTouched) {
            super.setProgress(progress)
        }
    }

    override fun setProgress(progress: Int, animate: Boolean) {
        if (!isTouched) {
            super.setProgress(progress, animate)
        }
    }


}