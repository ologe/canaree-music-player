package dev.olog.equalizer.equalizer

import android.media.audiofx.DynamicsProcessing
import android.os.Build
import androidx.annotation.RequiresApi
import dev.olog.core.entity.EqualizerBand
import dev.olog.core.entity.EqualizerPreset
import dev.olog.core.gateway.EqualizerGateway
import dev.olog.core.prefs.EqualizerPreferencesGateway
import dev.olog.equalizer.audioeffect.AudioEffects
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.P)
internal class DynamicProcessorImpl @Inject constructor(
    gateway: EqualizerGateway,
    prefs: EqualizerPreferencesGateway
) : AbsEqualizer(gateway, prefs), IEqualizer {

    companion object {
        private const val CHANNELS = 2
        private const val BANDS = 10
        private const val BAND_LIMIT = 12f
    }

    // todo find how to fix distorsion (same for normal equalizer)
    private fun createConfig(): DynamicsProcessing.Config {
        return DynamicsProcessing.Config.Builder(
            DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION,
            CHANNELS,
            true, BANDS,
            false, 0,
            false, 0,
            false
        ).apply {
            val currentPreset = gateway.getCurrentPreset()

            val eq = DynamicsProcessing.Eq(true, true, BANDS)

            for (index in 0 until eq.bandCount) {
                val currentBand = currentPreset.bands[index]
                val eqBand = DynamicsProcessing.EqBand(true, currentBand.frequency + BAND_LIMIT, currentBand.gain)
                eq.setBand(index, eqBand)
            }
            setPreEqAllChannelsTo(eq)

        }.build()
    }

    private var dynamicProcessing: DynamicsProcessing? = null

    override suspend fun onAudioSessionIdChanged(audioSessionId: Int) {
        release()
        try {
            dynamicProcessing = AudioEffects.createDynamicsProcessing(audioSessionId, createConfig())?.apply {
                enabled = prefs.isEqualizerEnabled()
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    override fun release() {
        try {
            dynamicProcessing?.release()
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
        dynamicProcessing = null
    }

    override fun setEnabled(enabled: Boolean) {
        try {
            dynamicProcessing?.enabled = enabled
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
        prefs.setEqualizerEnabled(enabled)
    }

    override suspend fun setCurrentPreset(preset: EqualizerPreset) {
        updateCurrentPresetIfCustom()
        prefs.setCurrentPresetId(preset.id)
        try {
            dynamicProcessing?.let {
                preset.bands.forEachIndexed { index, equalizerBand ->
                    val eq = it.getPreEqBandByChannelIndex(0, index)
                    eq.gain = equalizerBand.gain
                    it.setPreEqBandAllChannelsTo(index, eq)
                }
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    override fun getBandCount(): Int {
        try {
            return dynamicProcessing?.getPreEqByChannelIndex(0)?.bandCount ?: BANDS
        } catch (ex: Throwable) {
            ex.printStackTrace()
            return BANDS
        }
    }

    override fun getBandLevel(band: Int): Float {
        try {
            return dynamicProcessing?.getPreEqBandByChannelIndex(0, band)?.gain ?: 0f
        } catch (ex: Throwable) {
            ex.printStackTrace()
            return 0f
        }
    }

    override fun setBandLevel(band: Int, level: Float) {
        try {
            dynamicProcessing?.getPreEqBandByChannelIndex(0, band)?.let { eq ->
                eq.gain = level
                dynamicProcessing?.setPreEqBandAllChannelsTo(band, eq)
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    override fun getAllBandsCurrentLevel(): List<EqualizerBand> {
        try {
            val result = mutableListOf<EqualizerBand>()
            val bandCount = getBandCount()
            for (index in 0 until bandCount) {
                val eqBand = dynamicProcessing?.getPreEqBandByChannelIndex(0, index) ?: continue
                result.add(EqualizerBand(eqBand.gain, eqBand.cutoffFrequency - BAND_LIMIT))
            }
            return result
        } catch (ex: Throwable) {
            ex.printStackTrace()
            return emptyList()
        }
    }

    override fun getBandLimit(): Float = BAND_LIMIT

}