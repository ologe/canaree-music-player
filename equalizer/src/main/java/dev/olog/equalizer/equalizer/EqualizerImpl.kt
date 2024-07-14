package dev.olog.equalizer.equalizer

import dev.olog.core.entity.EqualizerBand
import dev.olog.core.entity.EqualizerPreset
import dev.olog.core.gateway.EqualizerGateway
import dev.olog.core.prefs.EqualizerPreferencesGateway
import dev.olog.equalizer.audioeffect.NormalizedEqualizer
import javax.inject.Inject

internal class EqualizerImpl @Inject constructor(
    gateway: EqualizerGateway,
    prefs: EqualizerPreferencesGateway

) : AbsEqualizer(gateway, prefs),
    IEqualizer {

    companion object {
        private const val BANDS = 5
        private const val BAND_LIMIT = 15f
    }

    private var equalizer: NormalizedEqualizer? = null

    override suspend fun onAudioSessionIdChanged(audioSessionId: Int) {
        release()
        try {
            equalizer = NormalizedEqualizer(audioSessionId).apply {
                enabled = prefs.isEqualizerEnabled()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun release() {
        try {
            equalizer?.release()
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
        equalizer = null
    }

    override fun setEnabled(enabled: Boolean) {
        try {
            equalizer?.enabled = enabled
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
        prefs.setEqualizerEnabled(enabled)
    }

    override suspend fun setCurrentPreset(preset: EqualizerPreset) {
        updateCurrentPresetIfCustom()
        prefs.setCurrentPresetId(preset.id)
        try {
            equalizer?.let { updatePresetInternal(preset) }
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    override fun getBandCount(): Int = BANDS

    override fun getBandLevel(band: Int): Float {
        try {
            return equalizer?.getBandLevel(band) ?: 0f
        } catch (ex: Throwable){
            ex.printStackTrace()
            return 0f
        }
    }

    override fun setBandLevel(band: Int, level: Float) {
        try {
            equalizer?.setBandLevel(band, level)
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    override fun getBandLimit(): Float = BAND_LIMIT

    override fun getAllBandsCurrentLevel(): List<EqualizerBand> {
        try {
            val result = mutableListOf<EqualizerBand>()
            for (bandIndex in 0 until BANDS) {
                val gain = equalizer?.getBandLevel(bandIndex) ?: continue
                val frequency = equalizer?.getBandFrequency(bandIndex) ?: continue
                result.add(EqualizerBand(gain, frequency))
            }
            return result
        } catch (ex: Throwable) {
            ex.printStackTrace()
            return emptyList()
        }
    }

    private fun updatePresetInternal(preset: EqualizerPreset) {
        try {
            preset.bands.forEachIndexed { index, equalizerBand ->
                setBandLevel(index, equalizerBand.gain)
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

}