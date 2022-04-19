package dev.olog.equalizer.equalizer

import android.media.audiofx.AudioEffect
import android.media.audiofx.DynamicsProcessing
import android.os.Build
import androidx.annotation.RequiresApi
import dev.olog.core.ApplicationScope
import dev.olog.core.entity.EqualizerBand
import dev.olog.core.entity.EqualizerPreset
import dev.olog.core.gateway.EqualizerGateway
import dev.olog.core.prefs.EqualizerPreferencesGateway
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.P)
internal class EqualizerImpl28 @Inject constructor(
    gateway: EqualizerGateway,
    prefs: EqualizerPreferencesGateway,
    private val applicationScope: ApplicationScope,
) : AbsEqualizer(gateway, prefs),
    IEqualizerInternal {

    companion object {
        private const val CHANNELS = 2
        private const val BANDS = 10
        private const val BAND_LIMIT = 12f
    }

    private suspend fun createConfig() = withContext(Dispatchers.IO) {
        DynamicsProcessing.Config.Builder(
            DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION,
            CHANNELS,
            true, BANDS,
            false, 0,
            false, 0,
            true
        ).apply {
            val currentPreset = gateway.getCurrentPreset()

            val eq = DynamicsProcessing.Eq(
                true, true,
                BANDS
            )

            for (index in 0 until eq.bandCount) {
                val currentBand = currentPreset.bands[index]
                val eqBand =
                    DynamicsProcessing.EqBand(true, currentBand.frequency + BAND_LIMIT, currentBand.gain)
                eq.setBand(index, eqBand)
            }
            setPreEqAllChannelsTo(eq)

        }.build()
    }

    private var dynamicProcessing: DynamicsProcessing? = null

    private var isImplementedByDevice = false

    init {
        for (queryEffect in AudioEffect.queryEffects()) {
            if (queryEffect.type == AudioEffect.EFFECT_TYPE_DYNAMICS_PROCESSING){
                isImplementedByDevice = true
            }
        }
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        if (!isImplementedByDevice){
            return
        }

        applicationScope.launch {
            release()
            dynamicProcessing = DynamicsProcessing(0, audioSessionId, createConfig()).apply {
                enabled = prefs.isEqualizerEnabled()
            }
        }
    }

    private fun release() {
        if (!isImplementedByDevice){
            return
        }

        dynamicProcessing?.release()
        dynamicProcessing = null
    }

    override fun onDestroy() {
        release()
    }

    override fun setEnabled(enabled: Boolean) {
        if (!isImplementedByDevice){
            return
        }

        dynamicProcessing?.enabled = enabled
        prefs.setEqualizerEnabled(enabled)
    }

    override suspend fun setCurrentPreset(preset: EqualizerPreset) {
        if (!isImplementedByDevice){
            return
        }

        updateCurrentPresetIfCustom()
        prefs.setCurrentPresetId(preset.id)
        dynamicProcessing?.let {
            preset.bands.forEachIndexed { index, equalizerBand ->
                val eq = it.getPreEqBandByChannelIndex(0, index)
                eq.gain = equalizerBand.gain
                it.setPreEqBandAllChannelsTo(index, eq)
            }
        }
    }

    override fun getBandCount(): Int = dynamicProcessing?.getPreEqByChannelIndex(0)?.bandCount ?: BANDS

    override fun getBandLevel(band: Int): Float {
        if (!isImplementedByDevice){
            return 0f
        }

        return dynamicProcessing?.getPreEqBandByChannelIndex(0, band)?.gain ?: 0f
    }

    override fun setBandLevel(band: Int, level: Float) {
        if (!isImplementedByDevice){
            return
        }

        dynamicProcessing?.getPreEqBandByChannelIndex(0, band)?.let { eq ->
            eq.gain = level
            dynamicProcessing?.setPreEqBandAllChannelsTo(band, eq)
        }
    }

    override fun getAllBandsCurrentLevel(): List<EqualizerBand> {
        if (!isImplementedByDevice){
            return emptyList()
        }

        val result = mutableListOf<EqualizerBand>()
        val eq = dynamicProcessing!!.getPreEqByChannelIndex(0)
        for (index in 0 until eq.bandCount) {
            val eqBand = dynamicProcessing!!.getPreEqBandByChannelIndex(0, index)
            result.add(EqualizerBand(eqBand.gain, eqBand.cutoffFrequency - BAND_LIMIT))
        }
        return result
    }

    override fun getBandLimit(): Float =
        BAND_LIMIT
}