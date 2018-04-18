package dev.olog.msc.music.service.equalizer

import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.decoder.DecoderCounters
import dev.olog.msc.dagger.scope.PerService
import dev.olog.msc.interfaces.equalizer.IBassBoost
import dev.olog.msc.interfaces.equalizer.IEqualizer
import dev.olog.msc.interfaces.equalizer.IReplayGain
import dev.olog.msc.interfaces.equalizer.IVirtualizer
import javax.inject.Inject

@PerService
class OnAudioSessionIdChangeListener @Inject constructor(
        private val equalizer: IEqualizer,
        private val virtualizer: IVirtualizer,
        private val bassBoost: IBassBoost,
        private val replayGain: IReplayGain

) : AudioRendererEventListener {

    override fun onAudioSinkUnderrun(bufferSize: Int, bufferSizeMs: Long, elapsedSinceLastFeedMs: Long) {}

    override fun onAudioEnabled(counters: DecoderCounters?) {}

    override fun onAudioInputFormatChanged(format: Format?) {}

    override fun onAudioDecoderInitialized(decoderName: String?, initializedTimestampMs: Long, initializationDurationMs: Long) {}

    override fun onAudioDisabled(counters: DecoderCounters?) {}

    override fun onAudioSessionId(audioSessionId: Int) {
        equalizer.onAudioSessionIdChanged(audioSessionId)
        virtualizer.onAudioSessionIdChanged(audioSessionId)
        bassBoost.onAudioSessionIdChanged(audioSessionId)
        replayGain.onAudioSessionIdChanged(audioSessionId)
    }

    fun release(){
        equalizer.release()
        virtualizer.release()
        bassBoost.release()
        replayGain.release()
    }
}