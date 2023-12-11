package dev.olog.media.widget

import android.widget.ProgressBar
import dev.olog.intents.AppConstants
import dev.olog.shared.flowInterval
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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

    private val progressFlow = MutableSharedFlow<Long>()

    override fun stopAutoIncrement(startMillis: Int) {
        incrementJob?.cancel()
        setProgress(progressBar, startMillis)
    }

    override fun startAutoIncrement(startMillis: Int, speed: Float) {
        stopAutoIncrement(startMillis)
        incrementJob = launch {
            flowInterval(
                AppConstants.PROGRESS_BAR_INTERVAL,
                TimeUnit.MILLISECONDS
            )
                .map { (it + 1) * AppConstants.PROGRESS_BAR_INTERVAL * speed + startMillis }
                .flowOn(Dispatchers.IO)
                .collect {
                    setProgress(progressBar, it.toInt())
                    progressFlow.tryEmit(it.toLong())
                }
        }
    }

    private fun setProgress(progressBar: ProgressBar, position: Int){
        progressBar.progress = position
    }

    override fun observeProgress(): Flow<Long> {
        return progressFlow
    }

    override fun onStateChanged(state: dev.olog.media.model.PlayerPlaybackState) {
        if (state.isPlaying) {
            startAutoIncrement(state.bookmark, state.playbackSpeed)
        } else {
            stopAutoIncrement(state.bookmark)
        }
    }


}