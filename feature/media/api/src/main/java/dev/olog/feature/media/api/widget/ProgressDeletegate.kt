package dev.olog.feature.media.api.widget

import android.widget.ProgressBar
import dev.olog.feature.media.api.MusicConstants
import dev.olog.feature.media.api.model.PlayerPlaybackState
import dev.olog.shared.flowInterval
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit

interface IProgressDeletegate {
    fun onStateChanged(state: PlayerPlaybackState)
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
            flowInterval(
                MusicConstants.PROGRESS_BAR_INTERVAL,
                TimeUnit.MILLISECONDS
            )
                .map { (it + 1) * MusicConstants.PROGRESS_BAR_INTERVAL * speed + startMillis }
                .flowOn(Dispatchers.IO)
                .collect {
                    setProgress(progressBar, it.toInt())
                    channel.trySend(it.toLong())
                }
        }
    }

    private fun setProgress(progressBar: ProgressBar, position: Int){
        progressBar.progress = position
    }

    override fun observeProgress(): Flow<Long> {
        return channel.asFlow()
    }

    override fun onStateChanged(state: PlayerPlaybackState) {
        if (state.isPlaying) {
            startAutoIncrement(state.bookmark, state.playbackSpeed)
        } else {
            stopAutoIncrement(state.bookmark)
        }
    }


}