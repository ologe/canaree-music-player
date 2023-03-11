package dev.olog.feature.media.impl

import android.app.Service
import android.util.Log
import com.google.android.exoplayer2.Player
import dev.olog.equalizer.bassboost.IBassBoost
import dev.olog.equalizer.equalizer.IEqualizer
import dev.olog.equalizer.virtualizer.IVirtualizer
import dev.olog.platform.extension.lifecycleScope
import kotlinx.coroutines.*
import javax.inject.Inject

class OnAudioSessionIdChangeListener @Inject constructor(
    private val service: Service,
    private val equalizer: IEqualizer,
    private val virtualizer: IVirtualizer,
    private val bassBoost: IBassBoost
) : Player.Listener {

    companion object {
        private val TAG = "SM:${OnAudioSessionIdChangeListener::class.java.simpleName}"
        const val DELAY = 500L
    }

    private var job: Job? = null

    private val hash by lazy { hashCode() }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        job?.cancel()
        job = service.lifecycleScope.launch {
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