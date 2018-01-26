package dev.olog.music_service.equalizer

import android.media.audiofx.BassBoost
import dev.olog.domain.interactor.prefs.EqualizerPrefsUseCase
import dev.olog.shared_android.interfaces.equalizer.IBassBoost
import javax.inject.Inject

class BassBoostImpl @Inject constructor(
        private val equalizerPrefsUseCase: EqualizerPrefsUseCase

) : IBassBoost {

    private var bassBoost = BassBoost(0, 1)

    override fun getStrength(): Int = bassBoost.roundedStrength.toInt()

    override fun setStrength(value: Int) {
        bassBoost.setStrength(value.toShort())
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        val settings = bassBoost.properties
        bassBoost.release()
        bassBoost = BassBoost(0, audioSessionId)
        bassBoost.enabled = equalizerPrefsUseCase.isEqualizerEnabled()
        settings?.let { bassBoost.properties = it }
    }

    override fun release() {
        equalizerPrefsUseCase.saveBassBoostSettings(bassBoost.properties.toString())
        bassBoost.release()
    }



}