package dev.olog.feature.media.impl

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.google.android.exoplayer2.audio.AudioListener
import dev.olog.equalizer.bassboost.IBassBoost
import dev.olog.equalizer.equalizer.IEqualizer
import dev.olog.equalizer.virtualizer.IVirtualizer
import dev.olog.injection.dagger.ServiceLifecycle
import kotlinx.coroutines.*
import javax.inject.Inject

internal class OnAudioSessionIdChangeListener @Inject constructor(
    @ServiceLifecycle private val lifecycle: Lifecycle,
    private val equalizer: IEqualizer,
    private val virtualizer: IVirtualizer,
    private val bassBoost: IBassBoost

) : AudioListener {

    companion object {
        @JvmStatic
        private val TAG = "SM:${OnAudioSessionIdChangeListener::class.java.simpleName}"
        internal const val DELAY = 500L
    }

    private var job: Job? = null

    private val hash by lazy { hashCode() }

    override fun onAudioSessionId(audioSessionId: Int) {
        job?.cancel()
        job = lifecycle.coroutineScope.launch {
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

    fun releaseAll() {
        equalizer.onDestroy()
        virtualizer.onDestroy()
        bassBoost.onDestroy()
    }

}