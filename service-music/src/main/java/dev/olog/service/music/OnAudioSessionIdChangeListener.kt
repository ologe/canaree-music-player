package dev.olog.service.music

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.audio.AudioListener
import dev.olog.injection.dagger.PerService
import dev.olog.injection.dagger.ServiceLifecycle
import dev.olog.injection.equalizer.IBassBoost
import dev.olog.injection.equalizer.IEqualizer
import dev.olog.injection.equalizer.IVirtualizer
import kotlinx.coroutines.*
import javax.inject.Inject

@PerService
internal class OnAudioSessionIdChangeListener @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    private val equalizer: IEqualizer,
    private val virtualizer: IVirtualizer,
    private val bassBoost: IBassBoost

) : AudioListener,
    DefaultLifecycleObserver,
    CoroutineScope by MainScope() {

    companion object {
        @JvmStatic
        private val TAG = "SM:${OnAudioSessionIdChangeListener::class.java.simpleName}"
        internal const val DELAY = 500L
    }

    private var job: Job? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        job?.cancel()
    }

    override fun onAudioSessionId(audioSessionId: Int) {
        job?.cancel()
        job = launch {
            delay(DELAY)
            onAudioSessionIdInternal(audioSessionId)
        }
    }

    private fun onAudioSessionIdInternal(audioSessionId: Int) {
        Log.v(TAG, "on audio session id changed =$audioSessionId")

        equalizer.onAudioSessionIdChanged(audioSessionId)
        virtualizer.onAudioSessionIdChanged(audioSessionId)
        bassBoost.onAudioSessionIdChanged(audioSessionId)
    }

    fun release() {
        Log.v(TAG, "release")
        equalizer.release()
        virtualizer.release()
        bassBoost.release()
    }
}