package dev.olog.media.widget

import android.os.SystemClock
import android.widget.ProgressBar
import dev.olog.intents.AppConstants
import dev.olog.shared.android.viewScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlin.math.roundToInt

interface IProgressDeletegate {
    fun onStateChanged(state: dev.olog.media.model.PlayerPlaybackState)
    fun startAutoIncrement(startMillis: Int, elapsedRealtime: Long, speed: Float)
    fun stopAutoIncrement(startMillis: Int)
    fun observeProgress(): Flow<Long>
}

class ProgressDeletegate(
    private val progressBar: ProgressBar
) : IProgressDeletegate {

    private var incrementJob: Job? = null

    private val channel = ConflatedBroadcastChannel<Long>()

    override fun stopAutoIncrement(startMillis: Int) {
        incrementJob?.cancel()
        setProgress(progressBar, startMillis)
        channel.offer(startMillis.toLong())
    }

    override fun startAutoIncrement(
        startMillis: Int,
        elapsedRealtime: Long,
        speed: Float,
    ) {
        incrementJob?.cancel()
        incrementJob = progressBar.viewScope.launch {
            while (isActive) {
                val newBookmark = computeBookmark(startMillis, elapsedRealtime, speed)
                setProgress(progressBar, newBookmark)
                channel.offer(newBookmark.toLong())
                delay(AppConstants.PROGRESS_BAR_INTERVAL)
            }
        }
    }

    private fun computeBookmark(startMillis: Int, elapsedRealtime: Long, speed: Float): Int {
        return startMillis + ((SystemClock.elapsedRealtime() - elapsedRealtime) * speed).roundToInt()
    }

    private fun setProgress(progressBar: ProgressBar, position: Int){
        progressBar.progress = position
    }

    override fun observeProgress(): Flow<Long> {
        return channel.asFlow()
    }

    override fun onStateChanged(state: dev.olog.media.model.PlayerPlaybackState) {
        if (state.isPlaying) {
            startAutoIncrement(state.bookmark, state.elapsedRealtime, state.playbackSpeed)
        } else {
            stopAutoIncrement(state.bookmark)
        }
    }


}