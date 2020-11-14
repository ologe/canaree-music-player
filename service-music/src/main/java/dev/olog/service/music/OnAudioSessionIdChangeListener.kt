package dev.olog.service.music

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.audio.AudioListener
import dev.olog.equalizer.bassboost.IBassBoost
import dev.olog.equalizer.equalizer.IEqualizer
import dev.olog.equalizer.virtualizer.IVirtualizer
import dev.olog.shared.android.coroutine.autoDisposeJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class OnAudioSessionIdChangeListener @Inject constructor(
    private val lifecycleOwner: LifecycleOwner,
    private val equalizer: IEqualizer,
    private val virtualizer: IVirtualizer,
    private val bassBoost: IBassBoost

) : AudioListener,
    DefaultLifecycleObserver {

    companion object {
        private val TAG = "SM:${OnAudioSessionIdChangeListener::class.java.simpleName}"
        internal const val DELAY = 500L
    }

    private var job by autoDisposeJob()

    private val hash by lazy { hashCode() }

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        job = null
    }

    override fun onAudioSessionId(audioSessionId: Int) {
        job = lifecycleOwner.lifecycleScope.launch {
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