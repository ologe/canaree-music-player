package dev.olog.equalizer.bassboost

import android.media.audiofx.BassBoost
import dev.olog.core.prefs.EqualizerPreferencesGateway
import dev.olog.equalizer.audioeffect.AudioEffects
import javax.inject.Inject

internal class BassBoostImpl @Inject constructor(
    private val equalizerPrefsUseCase: EqualizerPreferencesGateway
) : IBassBoost {

    private var bassBoost: BassBoost? = null

    override fun getStrength(): Int {
        try {
            return bassBoost?.roundedStrength?.toInt() ?: 0
        } catch (ex: Throwable){
            ex.printStackTrace()
            return 0
        }
    }

    override fun setStrength(value: Int) {
        try {
            bassBoost?.setStrength(value.toShort())?.also {
                val currentProperties = bassBoost?.properties?.toString()
                if (!currentProperties.isNullOrBlank()){
                    equalizerPrefsUseCase.saveBassBoostSettings(currentProperties)
                }
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    override fun setEnabled(enabled: Boolean) {
        try {
            bassBoost?.enabled = enabled
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    override suspend fun onAudioSessionIdChanged(audioSessionId: Int) {
        release()

        try {
            bassBoost = AudioEffects.createBassBoost(audioSessionId)?.apply {
                enabled = equalizerPrefsUseCase.isEqualizerEnabled()
                val settings = readSettings(equalizerPrefsUseCase.getBassBoostSettings())
                if (settings != null) {
                    // TODO gradually increase to avoid distorsion?
                    properties = settings
                }
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    override fun release(){
        try {
            bassBoost?.release()
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
        bassBoost = null
    }

    private fun readSettings(settings: String): BassBoost.Settings? {
        if (settings.isNotBlank()) {
            return BassBoost.Settings(settings)
        }
        return null
    }

}