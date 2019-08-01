package dev.olog.equalizer.impl

import android.media.audiofx.DynamicsProcessing
import android.os.Build
import androidx.annotation.RequiresApi
import dev.olog.core.entity.EqualizerBand
import dev.olog.core.entity.EqualizerPreset
import dev.olog.core.gateway.EqualizerGateway
import dev.olog.core.prefs.EqualizerPreferencesGateway
import dev.olog.equalizer.IEqualizer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.P)
internal class EqualizerImpl28 @Inject constructor(
    private val gateway: EqualizerGateway,
    private val prefs: EqualizerPreferencesGateway
) : IEqualizer, CoroutineScope by MainScope() {

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

            val eq = DynamicsProcessing.Eq(true, true,
                BANDS
            )

            for (index in 0 until BANDS) {
                val currentBand = currentPreset.bands[index]
                val eqBand =
                    DynamicsProcessing.EqBand(true, currentBand.frequency, currentBand.gain)
                eq.setBand(index, eqBand)
            }
            setPreEqAllChannelsTo(eq)

        }.build()
    }

    private var dynamicProcessing: DynamicsProcessing? = null

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        launch {
            release()
            dynamicProcessing = DynamicsProcessing(0, audioSessionId, createConfig()).apply {
                enabled = prefs.isEqualizerEnabled()
            }
        }
    }

    private fun release() {
        dynamicProcessing?.release()
        dynamicProcessing = null
    }

    override fun onDestroy() {
        release()
        // TODO save all temp configs
    }

    override fun setEnabled(enabled: Boolean) {
        dynamicProcessing?.enabled = enabled
        prefs.setEqualizerEnabled(enabled)
    }

    override fun getPresets(): List<EqualizerPreset> = gateway.getPresets()

    override fun observeCurrentPreset(): Flow<EqualizerPreset> {
        return gateway.observeCurrentPreset()
    }

    override fun getCurrentPreset(): EqualizerPreset {
        return gateway.getCurrentPreset()
    }

    override fun setCurrentPreset(preset: EqualizerPreset) {
        prefs.setCurrentPresetId(preset.id)
        dynamicProcessing?.let {
            preset.bands.forEachIndexed { index, equalizerBand ->
                val eq = it.getPreEqBandByChannelIndex(0, index)
                eq.gain = equalizerBand.gain
                it.setPreEqBandAllChannelsTo(index, eq)
            }
        }
    }

    override fun getBandCount(): Int = BANDS

    override fun getBandLevel(band: Int): Float {
        return dynamicProcessing?.getPreEqBandByChannelIndex(0, band)?.gain ?: 0f
    }

    override fun setBandLevel(band: Int, level: Float) {
        dynamicProcessing?.getPostEqBandByChannelIndex(0, band)?.let { eq ->
            eq.gain = level
            dynamicProcessing?.setPostEqBandAllChannelsTo(band, eq)
        }
    }

    override fun getAllBandsLevel(): List<EqualizerBand> {
        val result = mutableListOf<EqualizerBand>()
        for (index in 0 until CHANNELS){
            val eqBand = dynamicProcessing!!.getPreEqBandByChannelIndex(0, index)
            result.add(EqualizerBand(
                eqBand.gain, eqBand.cutoffFrequency
            ))
        }
        return result
    }

    override fun getBandLimit(): Float =
        BAND_LIMIT
}