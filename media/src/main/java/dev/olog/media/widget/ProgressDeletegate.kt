package dev.olog.media.widget

import android.annotation.SuppressLint
import android.widget.ProgressBar
import dev.olog.intents.AppConstants
import dev.olog.shared.autoDisposeJob
import dev.olog.shared.flowInterval
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit

interface IProgressDeletegate {
    fun onStateChanged(state: dev.olog.media.model.PlayerPlaybackState)
    fun startAutoIncrement(startMillis: Int, speed: Float)
    fun stopAutoIncrement(startMillis: Int)
    fun observeProgress(): Flow<Long>
}

// TODO cancel scope
class ProgressDeletegate(
    private val progressBar: ProgressBar
) : IProgressDeletegate,
    CoroutineScope by MainScope() {

    private var incrementJob by autoDisposeJob()

    private val channel = ConflatedBroadcastChannel<Long>()

    override fun stopAutoIncrement(startMillis: Int) {
        incrementJob = null
        setProgress(progressBar, startMillis)
    }

    @SuppressLint("ConcreteDispatcherIssue")
    override fun startAutoIncrement(startMillis: Int, speed: Float) {
        stopAutoIncrement(startMillis)
        incrementJob = flowInterval(
            AppConstants.PROGRESS_BAR_INTERVAL,
            TimeUnit.MILLISECONDS
        )
            .map { (it + 1) * AppConstants.PROGRESS_BAR_INTERVAL * speed + startMillis }
            .flowOn(Dispatchers.IO)
            .onEach {
                setProgress(progressBar, it.toInt())
            }.launchIn(this)
    }

    private fun setProgress(progressBar: ProgressBar, position: Int){
        progressBar.progress = position
        channel.offer(position.toLong())
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