package dev.olog.lib.media.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import dev.olog.lib.media.model.PlayerPlaybackState
import kotlinx.coroutines.flow.Flow

// TODO to presentatino-base
class CustomProgressBar(
    context: Context,
    attrs: AttributeSet
) : ProgressBar(context, attrs) {

    private val delegate = ProgressDelegate(this)

    init {
        max = Int.MAX_VALUE
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        delegate.stopAutoIncrement(0)
    }

    fun observeProgress(): Flow<Long> {
        return delegate.observeProgress()
    }

    fun onStateChanged(state: PlayerPlaybackState) {
        return delegate.onStateChanged(state)
    }
}