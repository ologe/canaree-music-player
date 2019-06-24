package dev.olog.msc.music.service.equalizer

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.decoder.DecoderCounters
import dev.olog.injection.equalizer.IBassBoost
import dev.olog.injection.equalizer.IEqualizer
import dev.olog.injection.equalizer.IVirtualizer
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.dagger.scope.PerService
import dev.olog.shared.extensions.unsubscribe
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@PerService
class OnAudioSessionIdChangeListener @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    private val equalizer: IEqualizer,
    private val virtualizer: IVirtualizer,
    private val bassBoost: IBassBoost

) : AudioRendererEventListener, DefaultLifecycleObserver {

    private var delayDisposable: Disposable? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        delayDisposable.unsubscribe()
    }

    override fun onAudioSinkUnderrun(bufferSize: Int, bufferSizeMs: Long, elapsedSinceLastFeedMs: Long) {}

    override fun onAudioEnabled(counters: DecoderCounters?) {}

    override fun onAudioInputFormatChanged(format: Format?) {}

    override fun onAudioDecoderInitialized(decoderName: String?, initializedTimestampMs: Long, initializationDurationMs: Long) {}

    override fun onAudioDisabled(counters: DecoderCounters?) {}

    override fun onAudioSessionId(audioSessionId: Int) {
        delayDisposable.unsubscribe()
        delayDisposable = Single.timer(500, TimeUnit.MILLISECONDS, Schedulers.computation())
                .map { audioSessionId }
                .subscribe(this::onAudioSessionIdInternal, Throwable::printStackTrace)
    }

    private fun onAudioSessionIdInternal(audioSessionId: Int){
        equalizer.onAudioSessionIdChanged(audioSessionId)
        virtualizer.onAudioSessionIdChanged(audioSessionId)
        bassBoost.onAudioSessionIdChanged(audioSessionId)
    }

    fun release(){
        equalizer.release()
        virtualizer.release()
        bassBoost.release()
    }
}