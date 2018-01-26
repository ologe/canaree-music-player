package dev.olog.music_service.equalizer

import android.media.audiofx.AutomaticGainControl
import dev.olog.domain.interactor.prefs.EqualizerPrefsUseCase
import dev.olog.shared_android.interfaces.equalizer.IReplayGain
import javax.inject.Inject

class ReplayGainImpl @Inject constructor(
        private val equalizerPrefsUseCase: EqualizerPrefsUseCase

) : IReplayGain {

    private var automaticGainControl : AutomaticGainControl? = null


    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        if (isImplementedByDevice()) {
            if (automaticGainControl != null){
                automaticGainControl?.release()
            }
            automaticGainControl = AutomaticGainControl.create(audioSessionId)
            automaticGainControl?.enabled = equalizerPrefsUseCase.isReplayGainEnabled()
        }
    }

    override fun isImplementedByDevice(): Boolean = AutomaticGainControl.isAvailable()

    override fun setEnabled(enabled: Boolean) {
        automaticGainControl?.enabled = enabled
    }

    override fun release() {
        automaticGainControl?.release()
    }
}