package dev.olog.service.music

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.audio.AudioListener
import dev.olog.shared.coroutines.MainScope
import dev.olog.shared.coroutines.autoDisposeJob
import dev.olog.equalizer.bassboost.IBassBoost
import dev.olog.equalizer.equalizer.IEqualizer
import dev.olog.equalizer.virtualizer.IVirtualizer
import dev.olog.injection.dagger.ServiceLifecycle
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

internal class OnAudioSessionIdChangeListener @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    private val equalizer: IEqualizer,
    private val virtualizer: IVirtualizer,
    private val bassBoost: IBassBoost

) : AudioListener,
    DefaultLifecycleObserver {

    companion object {
        @JvmStatic
        private val TAG = "SM:${OnAudioSessionIdChangeListener::class.java.simpleName}"
        internal const val DELAY = 500L
    }

    private val scope by MainScope()
    private var job by autoDisposeJob()

    private val hash by lazy { hashCode() }

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        scope.cancel()
    }

    override fun onAudioSessionId(audioSessionId: Int) {
        job = scope.launch {
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