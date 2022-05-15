package dev.olog.feature.media

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import com.google.android.exoplayer2.Player
import dev.olog.core.ServiceScope
import dev.olog.feature.equalizer.api.IBassBoost
import dev.olog.feature.equalizer.api.IEqualizer
import dev.olog.feature.equalizer.api.IVirtualizer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class OnAudioSessionIdChangeListener @Inject constructor(
    private val equalizer: IEqualizer,
    private val virtualizer: IVirtualizer,
    private val bassBoost: IBassBoost,
    private val serviceScope: ServiceScope,
) : Player.Listener,
    DefaultLifecycleObserver {

    companion object {
        internal const val DELAY = 500L
    }

    private var job: Job? = null

    private val hash by lazy { hashCode() }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        job?.cancel()
        job = serviceScope.launch {
            delay(DELAY)
            onAudioSessionIdInternal(audioSessionId)
        }
    }

    private fun onAudioSessionIdInternal(audioSessionId: Int) {
        equalizer.onAudioSessionIdChanged(hash, audioSessionId)
        virtualizer.onAudioSessionIdChanged(hash, audioSessionId)
        bassBoost.onAudioSessionIdChanged(hash, audioSessionId)
    }

    fun release() {
        equalizer.onDestroy(hash)
        virtualizer.onDestroy(hash)
        bassBoost.onDestroy(hash)
    }
}