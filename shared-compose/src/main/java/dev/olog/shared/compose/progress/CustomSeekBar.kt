package dev.olog.shared.compose.progress

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import dev.olog.media.model.PlayerPlaybackState
import dev.olog.shared.android.extensions.dipf
import dev.olog.shared.compose.R
import dev.olog.shared.widgets.drawable.SquigglyProgress
import kotlinx.coroutines.flow.Flow

class CustomSeekBar : AppCompatSeekBar, IProgressDeletegate {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.withStyledAttributes(attrs, R.styleable.CustomSeekBar) {
            val style = getInt(R.styleable.CustomSeekBar_seekbar_style, 0)
            when (style) {
                0 -> {
                    progressDrawable = ContextCompat.getDrawable(context, R.drawable.seek_bar_progress)
                    thumb = ContextCompat.getDrawable(context, R.drawable.seekbar_thumb_circle)
                }
                1 -> {
                    squiggly.strokeWidth = context.dipf(3)
                    progressDrawable = squiggly
                    thumb = ContextCompat.getDrawable(context, R.drawable.seekbar_thumb_rectangle)
                }
                2 -> {
                    progressDrawable = ContextCompat.getDrawable(context, R.drawable.seek_bar_progress_thick)
                    thumb = ContextCompat.getDrawable(context, R.drawable.seekbar_thumb_line)
                }
            }
            progressTintList = ContextCompat.getColorStateList(context, R.color.progressTint)
            thumbTintList = ContextCompat.getColorStateList(context, R.color.progressTint)
            progressBackgroundTintList = ContextCompat.getColorStateList(context, R.color.progressBackgroundTint)

        }
    }

    private var isTouched = false

    private var listener: OnSeekBarChangeListener? = null

    private val delegate: IProgressDeletegate by lazy { ProgressDeletegate(this) }

    private val squiggly = SquigglyProgress()

    init {
        if (!isInEditMode){
            max = Int.MAX_VALUE
        }
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
                // just stop the autoincrement job to avoid jumping of the seekbar
                stopAutoIncrement()
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

    override fun startAutoIncrement(startMillis: Int, elapsedRealtime: Long, speed: Float) {
        delegate.startAutoIncrement(startMillis, elapsedRealtime, speed)
    }

    override fun stopAutoIncrement(startMillis: Int) {
        delegate.stopAutoIncrement(startMillis)
    }

    override fun stopAutoIncrement() {
        delegate.stopAutoIncrement()
    }

    override fun onStateChanged(state: PlayerPlaybackState) {
        squiggly.animate = state.isPlaying
        delegate.onStateChanged(state)

    }

    override fun observeProgress(): Flow<Long> {
        return delegate.observeProgress()
    }
}