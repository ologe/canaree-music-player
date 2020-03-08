package dev.olog.media.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import dev.olog.media.model.PlayerPlaybackState
import kotlinx.coroutines.flow.Flow

class CustomProgressBar(
    context: Context,
    attrs: AttributeSet

) : ProgressBar(context, attrs) {

    private val delegate: IProgressDeletegate = ProgressDeletegate(this)

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