package dev.olog.service.music

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.audio.AudioListener
import dev.olog.equalizer.bassboost.IBassBoost
import dev.olog.equalizer.equalizer.IEqualizer
import dev.olog.equalizer.virtualizer.IVirtualizer
import dev.olog.shared.autoDisposeJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class OnAudioSessionIdChangeListener @Inject constructor(
    private val lifecycleOwner: LifecycleOwner,
    private val equalizer: IEqualizer,
    private val virtualizer: IVirtualizer,
    private val bassBoost: IBassBoost

) : AudioListener {

    companion object {
        internal const val DELAY = 500L
    }

    private var job by autoDisposeJob()

    private val hash by lazy { hashCode() }

    override fun onAudioSessionId(audioSessionId: Int) {
        job = lifecycleOwner.lifecycleScope.launch {
            delay(DELAY)
            onAudioSessionIdInternal(audioSessionId)
        }
    }

    private suspend fun onAudioSessionIdInternal(audioSessionId: Int) {
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