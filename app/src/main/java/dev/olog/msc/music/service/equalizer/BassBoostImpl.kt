package dev.olog.msc.music.service.equalizer

import android.media.audiofx.BassBoost
import dev.olog.msc.domain.interactor.prefs.EqualizerPrefsUseCase
import dev.olog.msc.interfaces.equalizer.IBassBoost
import javax.inject.Inject

class BassBoostImpl @Inject constructor(
        private val equalizerPrefsUseCase: EqualizerPrefsUseCase

) : IBassBoost {

    private var bassBoost : BassBoost? = null
    private var bassSettings = BassBoost.Settings()

    override fun getStrength(): Int {
        try {
            return bassBoost!!.roundedStrength.toInt()
        } catch (ex: Exception){
            return 0
        }
    }

    override fun setStrength(value: Int) {
        try {
            bassSettings.strength = value.toShort()
            bassBoost!!.setStrength(value.toShort())
        } catch (ex: Exception){}
    }

    override fun setEnabled(enabled: Boolean) {
        try {
            bassBoost!!.enabled = enabled
        } catch (ex: Exception){}
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        release()

        try {
            bassBoost = BassBoost(0, audioSessionId)
            bassBoost!!.enabled = equalizerPrefsUseCase.isEqualizerEnabled()
            if (bassSettings.toString().isNotBlank()){
                bassBoost!!.properties = bassSettings
            }
        } catch (ex: Exception){ }
    }

    override fun release() {
        equalizerPrefsUseCase.saveBassBoostSettings(bassSettings.toString())
        try {
            bassBoost!!.release()
        } catch (ex: Exception){}
    }

}