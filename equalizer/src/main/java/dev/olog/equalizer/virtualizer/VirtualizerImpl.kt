package dev.olog.equalizer.virtualizer

import android.media.audiofx.AudioEffect
import android.media.audiofx.Virtualizer
import dev.olog.core.prefs.EqualizerPreferencesGateway
import timber.log.Timber
import javax.inject.Inject

class VirtualizerImpl @Inject constructor(
    private val equalizerPrefsUseCase: EqualizerPreferencesGateway

) : IVirtualizerInternal {

    private var virtualizer: Virtualizer? = null

    private var isImplementedByDevice = false

    init {
        for (queryEffect in AudioEffect.queryEffects()) {
            if (queryEffect.type == AudioEffect.EFFECT_TYPE_VIRTUALIZER){
                isImplementedByDevice = true
            }
        }
    }

    override fun getStrength(): Int {
        if (!isImplementedByDevice){
            return 0
        }

        try {
            return virtualizer?.roundedStrength?.toInt() ?: 0
        } catch (ex: IllegalStateException){
            Timber.e(ex)
            // sometimes throws getParameter() called on uninitialized AudioEffect.
            return 0
        }
    }

    override fun setStrength(value: Int) {
        safeAction {
            virtualizer?.setStrength(value.toShort())?.also {
                val currentProperties = virtualizer?.properties?.toString()
                if (!currentProperties.isNullOrBlank()) {
                    equalizerPrefsUseCase.saveVirtualizerSettings(currentProperties)
                }
            }
        }
    }

    override fun setEnabled(enabled: Boolean) {
        safeAction {
            virtualizer?.enabled = enabled
        }
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        if (!isImplementedByDevice){
            return
        }

        release()

        try {
            virtualizer = Virtualizer(0, audioSessionId).apply {
                enabled = equalizerPrefsUseCase.isEqualizerEnabled()
                val lastProperties = equalizerPrefsUseCase.getVirtualizerSettings()
                if (lastProperties.isNotBlank()) {
                    properties = Virtualizer.Settings(lastProperties)
                }
            }
        } catch (ex: Throwable) {
            Timber.e(ex, "session id $audioSessionId")
        }
    }

    override fun onDestroy() {
        release()
    }

    private fun release() {
        safeAction {
            virtualizer?.release()
            virtualizer = null
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