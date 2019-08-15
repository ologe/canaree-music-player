package dev.olog.equalizer.impl

import android.content.Context
import android.media.audiofx.AudioEffect
import android.media.audiofx.BassBoost
import android.widget.Toast
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.prefs.EqualizerPreferencesGateway
import dev.olog.equalizer.IBassBoost
import javax.inject.Inject

class BassBoostImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val equalizerPrefsUseCase: EqualizerPreferencesGateway

) : IBassBoost {

    private var bassBoost: BassBoost? = null

    private var isImplementedByDevice = false

    init {
        for (queryEffect in AudioEffect.queryEffects()) {
            if (queryEffect.uuid == AudioEffect.EFFECT_TYPE_BASS_BOOST){
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
        if (!isImplementedByDevice){
            return
        }

        try {
            action()
        } catch (ex: IllegalStateException){
            ex.printStackTrace()
            // sometimes throws getParameter() called on uninitialized AudioEffect.
        }
    }

}