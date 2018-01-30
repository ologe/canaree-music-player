package dev.olog.music_service.equalizer

import android.media.audiofx.BassBoost
import dev.olog.domain.interactor.prefs.EqualizerPrefsUseCase
import dev.olog.shared_android.RootUtils
import dev.olog.shared_android.interfaces.equalizer.IBassBoost
import javax.inject.Inject

class BassBoostImpl @Inject constructor(
        private val equalizerPrefsUseCase: EqualizerPrefsUseCase

) : SafeAudioFx(), IBassBoost {

    private var bassBoost = BassBoost(0, 1)
    private var bassSettings = BassBoost.Settings()

    init {
        if (!RootUtils.isDeviceRooted()){
            val settings = equalizerPrefsUseCase.getBassBoostSettings()
            if (settings.isNotBlank()){
                bassSettings = BassBoost.Settings(settings)
            } else {
                bassSettings = bassBoost.properties
            }
        } else {
            bassBoost.release()
            isReleased = true
        }
    }

    override fun getStrength(): Int {
        return try {
            bassBoost.roundedStrength.toInt()
        } catch (ex: Exception){ 0 }
    }

    override fun setStrength(value: Int) {
        safeEdit {
            bassSettings.strength = value.toShort()
            bassBoost.setStrength(value.toShort())
        }
    }

    override fun setEnabled(enabled: Boolean) {
        safeEdit {
            bassBoost.enabled = enabled
        }
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        release()

        bassBoost = BassBoost(0, audioSessionId)
        isReleased = false
        bassBoost.enabled = equalizerPrefsUseCase.isEqualizerEnabled()
        if (bassSettings.toString().isNotBlank()){
            bassBoost.properties = bassSettings
        }
    }

    override fun release() {
        equalizerPrefsUseCase.saveBassBoostSettings(bassSettings.toString())
        super.release(bassBoost)
    }

}