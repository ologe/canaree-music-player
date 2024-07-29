package dev.olog.service.music

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import com.google.android.exoplayer2.Player
import dev.olog.core.ServiceLifecycle
import dev.olog.equalizer.EqualizerManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class OnAudioSessionIdChangeListener @Inject constructor(
    @ServiceLifecycle private val lifecycle: Lifecycle,
    private val equalizerManager: EqualizerManager,
) : Player.Listener {

    private val equalizer = equalizerManager.create(this)

    companion object {
        internal const val DELAY = 500L
    }

    private var job: Job? = null

    // todo not called anymore, so equalizer not working
    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        job?.cancel()
        job = lifecycle.coroutineScope.launch {
//            delay(DELAY) todo why is delay needed?
            onAudioSessionIdInternal(audioSessionId)
        }
    }

    private suspend fun onAudioSessionIdInternal(audioSessionId: Int) {
        equalizer.onAudioSessionIdChange(audioSessionId)
    }

    fun release() {
        equalizerManager.release(this)
    }
}