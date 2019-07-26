package dev.olog.injection.equalizer

import android.media.audiofx.Virtualizer
import dev.olog.core.prefs.EqualizerPreferencesGateway
import javax.inject.Inject

class VirtualizerImpl @Inject constructor(
    private val equalizerPrefsUseCase: EqualizerPreferencesGateway

) : IVirtualizer {

    private var virtualizer: Virtualizer? = null

    override fun getStrength(): Int {
        return virtualizer?.roundedStrength?.toInt() ?: 0
    }

    override fun setStrength(value: Int) {
        virtualizer?.setStrength(value.toShort())?.also {
            val currentProperties = virtualizer?.properties?.toString()
            if (!currentProperties.isNullOrBlank()) {
                equalizerPrefsUseCase.saveVirtualizerSettings(currentProperties)
            }
        }
    }

    override fun setEnabled(enabled: Boolean) {
        virtualizer?.enabled = enabled
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

    override fun release() {
        virtualizer?.release()
    }

}