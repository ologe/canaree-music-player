package dev.olog.music_service.equalizer

import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.decoder.DecoderCounters
import dev.olog.shared_android.interfaces.equalizer.IBassBoost
import dev.olog.shared_android.interfaces.equalizer.IEqualizer
import dev.olog.shared_android.interfaces.equalizer.IReplayGain
import dev.olog.shared_android.interfaces.equalizer.IVirtualizer

class OnAudioSessionIdChangeListener(
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