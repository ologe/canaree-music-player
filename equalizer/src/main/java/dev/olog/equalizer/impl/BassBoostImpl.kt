package dev.olog.equalizer.impl

import android.media.audiofx.BassBoost
import dev.olog.core.prefs.EqualizerPreferencesGateway
import dev.olog.equalizer.IBassBoost
import javax.inject.Inject

class BassBoostImpl @Inject constructor(
    private val equalizerPrefsUseCase: EqualizerPreferencesGateway

) : IBassBoost {

    private var bassBoost: BassBoost? = null

    override fun getStrength(): Int {
        try {
            return bassBoost?.roundedStrength?.toInt() ?: 0
        } catch (ex: IllegalStateException){
            ex.printStackTrace()
            // sometimes throws getParameter() called on uninitialized AudioEffect.
            return 0
        }
    }

    override fun setStrength(value: Int) {
        safeAction {
            bassBoost?.setStrength(value.toShort())?.also {
                val currentProperties = bassBoost?.properties?.toString()
                if (!currentProperties.isNullOrBlank()){
                    equalizerPrefsUseCase.saveBassBoostSettings(currentProperties)
                }
            }
        }
    }

    override fun setEnabled(enabled: Boolean) {
        safeAction {
            bassBoost?.enabled = enabled
        }
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        release()

        try {
            bassBoost = BassBoost(0, audioSessionId).apply {
                enabled = equalizerPrefsUseCase.isEqualizerEnabled()
                val lastProperties = equalizerPrefsUseCase.getBassBoostSettings()
                if (lastProperties.isNotBlank()) {
                    properties = BassBoost.Settings(lastProperties)
                }
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    override fun onDestroy() {
        release()
    }

    private fun release(){
        safeAction {
            bassBoost?.release()
            bassBoost = null
        }
    }

    private fun safeAction(action: () -> Unit){
        try {
            action()
        } catch (ex: IllegalStateException){
            ex.printStackTrace()
            // sometimes throws getParameter() called on uninitialized AudioEffect.
        }
    }

}