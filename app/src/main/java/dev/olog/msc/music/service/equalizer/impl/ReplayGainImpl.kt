package dev.olog.msc.music.service.equalizer.impl

import android.media.audiofx.AutomaticGainControl
import dev.olog.msc.domain.interactor.prefs.EqualizerPrefsUseCase
import dev.olog.msc.music.service.equalizer.IReplayGain
import javax.inject.Inject

class ReplayGainImpl @Inject constructor(
        private val equalizerPrefsUseCase: EqualizerPrefsUseCase

) : IReplayGain {

    private var automaticGainControl : AutomaticGainControl? = null

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        if (isImplementedByDevice()) {
            release()
            try {
                automaticGainControl = AutomaticGainControl.create(audioSessionId)
                automaticGainControl?.enabled = equalizerPrefsUseCase.isReplayGainEnabled()
            } catch (ex: Exception){ }
        }
    }

    override fun isImplementedByDevice(): Boolean = AutomaticGainControl.isAvailable()

    override fun setEnabled(enabled: Boolean) {
        try {
            automaticGainControl?.enabled = enabled
        } catch (ex: Exception){ }
    }

    override fun release() {
        try {
            automaticGainControl?.release()
        } catch (ex: Exception){}
    }

}