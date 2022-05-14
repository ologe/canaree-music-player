package dev.olog.ui.progress

import android.widget.ProgressBar
import dev.olog.feature.media.api.model.PlayerPlaybackState
import dev.olog.shared.extension.coroutineScope
import dev.olog.shared.flowInterval
import dev.olog.ui.progress.IProgressDeletegate.Companion.PROGRESS_BAR_INTERVAL
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

interface IProgressDeletegate {

    companion object {
        const val PROGRESS_BAR_INTERVAL = 50L
    }

    fun onStateChanged(state: PlayerPlaybackState)
    fun startAutoIncrement(startMillis: Int, speed: Float)
    fun stopAutoIncrement(startMillis: Int)
    fun observeProgress(): Flow<Long>
}

class ProgressDeletegate(
    private val progressBar: ProgressBar
) : IProgressDeletegate {

    private var incrementJob: Job? = null

    private val progressPublisher = MutableStateFlow<Long?>(null)

    override fun stopAutoIncrement(startMillis: Int) {
        incrementJob?.cancel()
        setProgress(progressBar, startMillis)
    }

    override fun startAutoIncrement(startMillis: Int, speed: Float) {
        stopAutoIncrement(startMillis)
        incrementJob = progressBar.coroutineScope.launch {
            flowInterval(
                PROGRESS_BAR_INTERVAL,
                TimeUnit.MILLISECONDS
            )
                .map { (it + 1) * PROGRESS_BAR_INTERVAL * speed + startMillis }
                .collect {
                    setProgress(progressBar, it.toInt())
                    progressPublisher.value = it.toLong()
                }
        }
    }

    private fun setProgress(progressBar: ProgressBar, position: Int){
        progressBar.progress = position
    }

    override fun observeProgress(): Flow<Long> = progressPublisher.filterNotNull()

    override fun onStateChanged(state: PlayerPlaybackState) {
        if (state.isPlaying) {
            startAutoIncrement(state.bookmark, state.playbackSpeed)
        } else {
            stopAutoIncrement(state.bookmark)
        }
    }


}