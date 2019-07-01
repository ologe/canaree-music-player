package dev.olog.shared.widgets.progressbar

import android.content.Context
import android.os.SystemClock
import android.support.v4.media.session.PlaybackStateCompat
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import kotlinx.coroutines.flow.Flow

class CustomSeekBar(
    context: Context,
    attrs: AttributeSet

) : AppCompatSeekBar(context, attrs),
    IProgressDeletegate {

    private var isTouched = false

    private var listener: OnSeekBarChangeListener? = null

    private val delegate: IProgressDeletegate =
        ProgressDeletegate(this)

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
        if (isAttachedToWindow) {
            setOnSeekBarChangeListener(listener)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setOnSeekBarChangeListener(listener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setOnSeekBarChangeListener(null)
        stopAutoIncrement(0)
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

    override fun startAutoIncrement(startMillis: Int, speed: Float) {
        delegate.startAutoIncrement(startMillis, speed)
    }

    override fun stopAutoIncrement(startMillis: Int) {
        delegate.stopAutoIncrement(startMillis)
    }

    override fun onStateChanged(state: PlaybackStateCompat){
        delegate.onStateChanged(state)

    }

    override fun observeProgress(): Flow<Long> {
        return delegate.observeProgress()
    }
}