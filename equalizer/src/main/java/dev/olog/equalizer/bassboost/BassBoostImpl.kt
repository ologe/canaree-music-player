package dev.olog.equalizer.bassboost

import android.media.audiofx.AudioEffect
import android.media.audiofx.BassBoost
import dev.olog.core.prefs.EqualizerPreferencesGateway
import timber.log.Timber
import javax.inject.Inject

class BassBoostImpl @Inject constructor(
    private val equalizerPrefsUseCase: EqualizerPreferencesGateway

) : IBassBoostInternal {

    private var bassBoost: BassBoost? = null

    private var isImplementedByDevice = false

    init {
        for (queryEffect in AudioEffect.queryEffects()) {
            if (queryEffect.type == AudioEffect.EFFECT_TYPE_BASS_BOOST){
                isImplementedByDevice = true
            }
        }
    }

    override fun getStrength(): Int {
        if (!isImplementedByDevice){
            return 0
        }
        try {
            return bassBoost?.roundedStrength?.toInt() ?: 0
        } catch (ex: IllegalStateException){
            Timber.e(ex)
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
        if (!isImplementedByDevice){
            return
        }

        release()

        try {
            bassBoost = BassBoost(0, audioSessionId).apply {
                enabled = equalizerPrefsUseCase.isEqualizerEnabled()
                val lastProperties = equalizerPrefsUseCase.getBassBoostSettings()
                if (lastProperties.isNotBlank()) {
                    properties = BassBoost.Settings(lastProperties)
                }
            }
        } catch (ex: Exception) {
            Timber.e(ex, "session id $audioSessionId")
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
        if (!isImplementedByDevice){
            return
        }

        try {
            action()
        } catch (ex: IllegalStateException){
            Timber.e(ex)
            // sometimes throws getParameter() called on uninitialized AudioEffect.
        }
    }

}