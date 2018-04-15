package dev.olog.msc.presentation.widget

import android.content.Context
import android.support.v7.widget.AppCompatSeekBar
import android.util.AttributeSet
import android.widget.SeekBar

class CustomSeekBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

): AppCompatSeekBar(context, attrs) {

    private var isTouched = false

    private var listener: SeekBar.OnSeekBarChangeListener? = null

    fun setListener(onProgressChanged: (Int) -> Unit,
                    onStartTouch: (Int) -> Unit,
                    onStopTouch: (Int) -> Unit){

        listener = object : SeekBar.OnSeekBarChangeListener {
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
        if (!isTouched){
            super.setProgress(progress)
        }
    }

    override fun setProgress(progress: Int, animate: Boolean) {
        if (!isTouched){
            super.setProgress(progress, animate)
        }
    }



}