package dev.olog.ui.progress

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import dev.olog.feature.media.api.model.PlayerPlaybackState
import kotlinx.coroutines.flow.Flow

class CustomProgressBar(
    context: Context,
    attrs: AttributeSet

) : ProgressBar(context, attrs),
    IProgressDeletegate {

    private val delegate: IProgressDeletegate = ProgressDeletegate(this)

    init {
        max = Int.MAX_VALUE
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAutoIncrement(0)
    }

    override fun startAutoIncrement(startMillis: Int, speed: Float) {
        delegate.startAutoIncrement(startMillis, speed)
    }

    override fun stopAutoIncrement(startMillis: Int) {
        delegate.stopAutoIncrement(startMillis)
    }

    override fun observeProgress(): Flow<Long> {
        return delegate.observeProgress()
    }

    override fun onStateChanged(state: PlayerPlaybackState) {
        return delegate.onStateChanged(state)
    }
}