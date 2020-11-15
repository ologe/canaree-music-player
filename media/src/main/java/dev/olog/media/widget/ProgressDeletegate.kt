package dev.olog.media.widget

import android.widget.ProgressBar
import dev.olog.intents.AppConstants
import dev.olog.shared.android.coroutine.autoDisposeJob
import dev.olog.shared.android.coroutine.viewScope
import dev.olog.shared.FlowInterval
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

interface IProgressDeletegate {
    fun onStateChanged(state: dev.olog.media.model.PlayerPlaybackState)
    fun startAutoIncrement(startMillis: Int, speed: Float)
    fun stopAutoIncrement(startMillis: Int)
    fun observeProgress(): Flow<Long>
}

class ProgressDeletegate(
    private val progressBar: ProgressBar
) : IProgressDeletegate {

    private var incrementJob by autoDisposeJob()

    private val channel = ConflatedBroadcastChannel<Long>()

    override fun stopAutoIncrement(startMillis: Int) {
        incrementJob = null
        setProgress(progressBar, startMillis)
    }

    override fun startAutoIncrement(startMillis: Int, speed: Float) {
        stopAutoIncrement(startMillis)
        incrementJob = FlowInterval(AppConstants.PROGRESS_BAR_INTERVAL)
            .map { (it + 1) * AppConstants.PROGRESS_BAR_INTERVAL.toLongMilliseconds() * speed + startMillis }
            .flowOn(Dispatchers.IO)
            .onEach {
                setProgress(progressBar, it.toInt())
                channel.offer(it.toLong())
            }.launchIn(progressBar.viewScope)
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