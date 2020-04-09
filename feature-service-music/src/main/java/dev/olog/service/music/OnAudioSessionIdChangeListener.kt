package dev.olog.service.music

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import dev.olog.core.dagger.ServiceLifecycle
import dev.olog.lib.equalizer.bassboost.IBassBoost
import dev.olog.lib.equalizer.equalizer.IEqualizer
import dev.olog.lib.equalizer.virtualizer.IVirtualizer
import dev.olog.shared.coroutines.autoDisposeJob
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject

// TODO refactor
internal class OnAudioSessionIdChangeListener @Inject constructor(
    @ServiceLifecycle private val lifecycle: Lifecycle,
    private val equalizer: IEqualizer,
    private val virtualizer: IVirtualizer,
    private val bassBoost: IBassBoost

) {

    companion object {
        @JvmStatic
        private val TAG = "SM:${OnAudioSessionIdChangeListener::class.java.simpleName}"
        internal const val DELAY = 500L
    }

    private var job by autoDisposeJob()

    private val hash by lazy { hashCode() }

    fun onAudioSessionId(audioSessionId: Int) {
        job = lifecycle.coroutineScope.launchWhenResumed {
            delay(DELAY)
            onAudioSessionIdInternal(audioSessionId)
        }
    }

    private fun onAudioSessionIdInternal(audioSessionId: Int) {
        Timber.v("$TAG on audio session id changed =$audioSessionId")

        equalizer.onAudioSessionIdChanged(hash, audioSessionId)
        virtualizer.onAudioSessionIdChanged(hash, audioSessionId)
        bassBoost.onAudioSessionIdChanged(hash, audioSessionId)
    }

    // TODO is release called ??
    fun release() {
        Timber.v("$TAG onDestroy")
        equalizer.onDestroy(hash)
        virtualizer.onDestroy(hash)
        bassBoost.onDestroy(hash)
    }
}