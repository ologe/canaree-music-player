package dev.olog.shared.compose.progress

import android.os.SystemClock
import android.widget.ProgressBar
import dev.olog.shared.android.viewScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.math.roundToInt

private const val PROGRESS_BAR_INTERVAL = 50L

interface IProgressDeletegate {
    fun onStateChanged(state: dev.olog.media.model.PlayerPlaybackState)
    fun startAutoIncrement(startMillis: Int, elapsedRealtime: Long, speed: Float)
    fun stopAutoIncrement(startMillis: Int)
    fun stopAutoIncrement()
    fun observeProgress(): Flow<Long>
}

class ProgressDeletegate(
    private val progressBar: ProgressBar
) : IProgressDeletegate {

    private var incrementJob: Job? = null

    private val flow = MutableStateFlow<Long>(0)

    override fun stopAutoIncrement(startMillis: Int) {
        incrementJob?.cancel()
        setProgress(progressBar, startMillis)
        flow.value = startMillis.toLong()
    }

    override fun stopAutoIncrement() {
        incrementJob?.cancel()
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
                flow.value = newBookmark.toLong()
                delay(PROGRESS_BAR_INTERVAL)
            }
        }
    }

    private fun computeBookmark(startMillis: Int, elapsedRealtime: Long, speed: Float): Int {
        return startMillis + ((SystemClock.elapsedRealtime() - elapsedRealtime) * speed).roundToInt()
    }

    private fun setProgress(progressBar: ProgressBar, position: Int){
        progressBar.progress = position
    }

    override fun observeProgress(): Flow<Long> = flow

    override fun onStateChanged(state: dev.olog.media.model.PlayerPlaybackState) {
        if (state.isPlaying) {
            startAutoIncrement(state.bookmark, state.elapsedRealtime, state.playbackSpeed)
        } else {
            stopAutoIncrement(state.bookmark)
        }
    }


}