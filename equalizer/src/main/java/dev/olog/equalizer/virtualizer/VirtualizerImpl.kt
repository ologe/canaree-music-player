package dev.olog.equalizer.virtualizer

import android.media.audiofx.Virtualizer
import dev.olog.core.prefs.EqualizerPreferencesGateway
import dev.olog.equalizer.audioeffect.AudioEffects
import javax.inject.Inject

internal class VirtualizerImpl @Inject constructor(
    private val equalizerPrefsUseCase: EqualizerPreferencesGateway
) : IVirtualizer {

    private var virtualizer: Virtualizer? = null

    override fun getStrength(): Int {
        try {
            return virtualizer?.roundedStrength?.toInt() ?: 0
        } catch (ex: Throwable) {
            ex.printStackTrace()
            return 0
        }
    }

    override fun setStrength(value: Int) {
        try {
            virtualizer?.setStrength(value.toShort())?.also {
                val currentProperties = virtualizer?.properties?.toString()
                if (!currentProperties.isNullOrBlank()) {
                    equalizerPrefsUseCase.saveVirtualizerSettings(currentProperties)
                }
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    override fun setEnabled(enabled: Boolean) {
        try {
            virtualizer?.enabled = enabled
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    override suspend fun onAudioSessionIdChanged(audioSessionId: Int) {
        release()

        try {
            virtualizer = AudioEffects.createVirtualizer(audioSessionId)?.apply {
                enabled = equalizerPrefsUseCase.isEqualizerEnabled()
                val settings = readSettings(equalizerPrefsUseCase.getVirtualizerSettings())
                if (settings != null) {
                    // TODO gradually increase to avoid distorsion?
                    properties = settings
                }
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    override fun release() {
        try {
            virtualizer?.release()
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
        virtualizer = null
    }

    private fun readSettings(settings: String): Virtualizer.Settings? {
        if (settings.isNotBlank()) {
            return Virtualizer.Settings(settings)
        }
        return null
    }

}