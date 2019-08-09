package dev.olog.media.widget

import android.widget.ProgressBar
import dev.olog.intents.AppConstants
import dev.olog.shared.android.utils.isNougat
import dev.olog.shared.flowInterval
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit

interface IProgressDeletegate {
    fun onStateChanged(state: dev.olog.media.model.PlayerPlaybackState)
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
        setProgress(progressBar, startMillis)
    }

    override fun startAutoIncrement(startMillis: Int, speed: Float) {
        stopAutoIncrement(startMillis)
        incrementJob = launch {
            var current = 1
            while (true){
                delay(AppConstants.PROGRESS_BAR_INTERVAL)
                current++
                computeProgress(++current, startMillis, speed)
            }
        }
    }

    private fun computeProgress(time: Int, startMillis: Int, speed: Float){
        val progress = (time + 1) * AppConstants.PROGRESS_BAR_INTERVAL * speed + startMillis
        setProgress(progressBar, progress.toInt())
        channel.offer(progress.toLong())
    }

    private fun setProgress(progressBar: ProgressBar, position: Int){
        progressBar.progress = position
    }

    override fun observeProgress(): Flow<Long> {
        return channel.asFlow()
    }

    override fun onStateChanged(state: dev.olog.media.model.PlayerPlaybackState) {
        if (state.isPlaying) {
            startAutoIncrement(state.bookmark, state.playbackSpeed)
        } else {
            stopAutoIncrement(state.bookmark)
        }
    }


}