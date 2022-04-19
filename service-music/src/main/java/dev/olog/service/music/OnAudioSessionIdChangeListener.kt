package dev.olog.service.music

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import com.google.android.exoplayer2.Player
import dev.olog.core.ServiceScope
import dev.olog.equalizer.bassboost.IBassBoost
import dev.olog.equalizer.equalizer.IEqualizer
import dev.olog.equalizer.virtualizer.IVirtualizer
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
        @JvmStatic
        private val TAG = "SM:${OnAudioSessionIdChangeListener::class.java.simpleName}"
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
        Log.v(TAG, "on audio session id changed =$audioSessionId")

        equalizer.onAudioSessionIdChanged(hash, audioSessionId)
        virtualizer.onAudioSessionIdChanged(hash, audioSessionId)
        bassBoost.onAudioSessionIdChanged(hash, audioSessionId)
    }

    fun release() {
        Log.v(TAG, "onDestroy")
        equalizer.onDestroy(hash)
        virtualizer.onDestroy(hash)
        bassBoost.onDestroy(hash)
    }
}