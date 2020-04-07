package dev.olog.lib.media.widget

import android.annotation.SuppressLint
import android.widget.ProgressBar
import dev.olog.core.coroutines.viewScope
import dev.olog.intents.AppConstants
import dev.olog.lib.media.model.PlayerPlaybackState
import dev.olog.shared.coroutines.autoDisposeJob
import dev.olog.shared.coroutines.flowInterval
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit

internal class ProgressDelegate(
    private val progressBar: ProgressBar
) {

    private var incrementJob by autoDisposeJob()

    private val channel = ConflatedBroadcastChannel<Long>()

    fun stopAutoIncrement(startMillis: Int) {
        incrementJob = null
        setProgress(progressBar, startMillis)
    }

    @SuppressLint("ConcreteDispatcherIssue")
    fun startAutoIncrement(startMillis: Int, speed: Float) {
        stopAutoIncrement(startMillis)
        incrementJob = flowInterval(
            AppConstants.PROGRESS_BAR_INTERVAL,
            TimeUnit.MILLISECONDS
        )
            .map { (it + 1) * AppConstants.PROGRESS_BAR_INTERVAL * speed + startMillis }
            .flowOn(Dispatchers.IO)
            .onEach {
                setProgress(progressBar, it.toInt())
            }.launchIn(progressBar.viewScope)
    }

    private fun setProgress(progressBar: ProgressBar, position: Int){
        progressBar.progress = position
        channel.offer(position.toLong())
    }

    fun observeProgress(): Flow<Long> {
        return channel.asFlow()
    }

    fun onStateChanged(state: PlayerPlaybackState) {
        if (state.isPlaying) {
            startAutoIncrement(state.bookmark, state.playbackSpeed)
        } else {
            stopAutoIncrement(state.bookmark)
        }
    }


}