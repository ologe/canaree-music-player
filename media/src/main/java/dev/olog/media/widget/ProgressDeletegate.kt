package dev.olog.media.widget

import android.widget.ProgressBar
import dev.olog.shared.AppConstants
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

    override fun onStateChanged(state: dev.olog.media.model.PlayerPlaybackState) {
        if (state.isPlaying) {
            startAutoIncrement(state.bookmark, state.playbackSpeed)
        } else {
            stopAutoIncrement(state.bookmark)
        }
    }


}