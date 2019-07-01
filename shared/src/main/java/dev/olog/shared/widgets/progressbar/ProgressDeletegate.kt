package dev.olog.shared.widgets.progressbar

import android.os.SystemClock
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.ProgressBar
import dev.olog.shared.AppConstants
import dev.olog.shared.flowInterval
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit

interface IProgressDeletegate {
    fun onStateChanged(state: PlaybackStateCompat)
    fun startAutoIncrement(startMillis: Int, speed: Float)
    fun stopAutoIncrement(startMillis: Int)
    fun observeProgress(): Flow<Long>
}

class ProgressDeletegate(
    private val progressBar: ProgressBar
) : IProgressDeletegate,
    CoroutineScope by MainScope() {

    private var incrementJob: Job? = null

    private val channel = ConflatedBroadcastChannel<Long>()

    override fun stopAutoIncrement(startMillis: Int) {
        incrementJob?.cancel()
        progressBar.progress = startMillis
    }

    override fun startAutoIncrement(startMillis: Int, speed: Float) {
        stopAutoIncrement(startMillis)
        incrementJob = launch {
            flowInterval(AppConstants.PROGRESS_BAR_INTERVAL, TimeUnit.MILLISECONDS)
                .map { (it + 1) * AppConstants.PROGRESS_BAR_INTERVAL * speed + startMillis }
                .flowOn(Dispatchers.IO)
                .collect {
                    progressBar.progress = it.toInt()
                    channel.send(it.toLong())
                }
        }
    }

    override fun observeProgress(): Flow<Long> {
        return channel.asFlow()
    }

    override fun onStateChanged(state: PlaybackStateCompat) {
        if (state.state == PlaybackStateCompat.STATE_PLAYING) {
            startAutoIncrement(state.extractBookmark(), state.playbackSpeed)
        } else {
            stopAutoIncrement(state.extractBookmark())
        }
    }

    private fun PlaybackStateCompat.extractBookmark(): Int {
        var bookmark = this.position

        if (this.state == PlaybackStateCompat.STATE_PLAYING) {
            val timeDelta = SystemClock.elapsedRealtime() - this.lastPositionUpdateTime
            bookmark += (timeDelta * this.playbackSpeed).toLong()
        }
        return bookmark.toInt()
    }
}