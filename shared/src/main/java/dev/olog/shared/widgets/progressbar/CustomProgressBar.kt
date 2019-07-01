package dev.olog.shared.widgets.progressbar

import android.content.Context
import android.support.v4.media.session.PlaybackStateCompat
import android.util.AttributeSet
import android.widget.ProgressBar
import kotlinx.coroutines.flow.Flow

class CustomProgressBar(
    context: Context,
    attrs: AttributeSet

) : ProgressBar(context, attrs),
    IProgressDeletegate {

    private val delegate: IProgressDeletegate =
        ProgressDeletegate(this)

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

    override fun onStateChanged(state: PlaybackStateCompat) {
        return delegate.onStateChanged(state)
    }
}