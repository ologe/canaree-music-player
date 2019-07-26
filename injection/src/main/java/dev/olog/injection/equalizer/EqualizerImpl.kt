package dev.olog.injection.equalizer

import android.media.audiofx.Equalizer
import dev.olog.core.prefs.EqualizerPreferencesGateway
import javax.inject.Inject

class EqualizerImpl @Inject constructor(
    private val equalizerPrefsUseCase: EqualizerPreferencesGateway

) : IEqualizer {

    private var equalizer: Equalizer? = null
    private val listeners = mutableListOf<IEqualizer.Listener>()

    override fun addListener(listener: IEqualizer.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: IEqualizer.Listener) {
        listeners.remove(listener)
    }

    override fun getBandLevel(band: Int): Float {
        return equalizer?.getBandLevel(band.toShort())?.toFloat() ?: 0f
    }

    override fun setBandLevel(band: Int, level: Float) {
        equalizer?.setBandLevel(band.toShort(), level.toShort())?.also {
            save()
        }
    }

    override fun setPreset(position: Int) {
        equalizer?.usePreset(position.toShort())?.also {
            listeners.forEach {
                for (band in 0 until equalizer!!.numberOfBands) {
                    val level = equalizer!!.getBandLevel(band.toShort()) / 100
                    it.onPresetChange(band, level.toFloat())
                }
            }

            save()
        }
    }

    override fun getPresets(): List<String> {
        return try {
            (0 until equalizer!!.numberOfPresets)
                .map { equalizer!!.getPresetName(it.toShort()) }
        } catch (ex: Throwable) {
            return emptyList()
        }
    }

    override fun getCurrentPreset(): Int {
        return equalizer?.currentPreset?.toInt() ?: 0
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        release()

        equalizer = Equalizer(0, audioSessionId).apply {
            enabled = equalizerPrefsUseCase.isEqualizerEnabled()
            val lastProperties = equalizerPrefsUseCase.getEqualizerSettings()
            if (lastProperties.isNotBlank()) {
                properties = Equalizer.Settings(lastProperties)
            }
        }
    }

    override fun setEnabled(enabled: Boolean) {
        equalizer?.enabled = enabled
    }

    override fun release() {
        equalizer?.release()
    }

    private fun save() {
        val currentProperties = equalizer?.properties?.toString()
        if (!currentProperties.isNullOrBlank()) {
            equalizerPrefsUseCase.saveEqualizerSettings(currentProperties)
        }
    }

}