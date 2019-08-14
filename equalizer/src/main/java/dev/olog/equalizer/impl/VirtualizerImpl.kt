package dev.olog.equalizer.impl

import android.media.audiofx.Virtualizer
import dev.olog.core.prefs.EqualizerPreferencesGateway
import dev.olog.equalizer.IVirtualizer
import javax.inject.Inject

class VirtualizerImpl @Inject constructor(
    private val equalizerPrefsUseCase: EqualizerPreferencesGateway

) : IVirtualizer {

    private var virtualizer: Virtualizer? = null

    override fun getStrength(): Int {
        try {
            return virtualizer?.roundedStrength?.toInt() ?: 0
        } catch (ex: IllegalStateException){
            ex.printStackTrace()
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
            ex.printStackTrace()
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
        try {
            action()
        } catch (ex: IllegalStateException){
            ex.printStackTrace()
            // sometimes throws getParameter() called on uninitialized AudioEffect.
        }
    }

}