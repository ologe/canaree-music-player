package dev.olog.equalizer.equalizer

import android.media.audiofx.AudioEffect
import dev.olog.core.entity.EqualizerBand
import dev.olog.core.entity.EqualizerPreset
import dev.olog.core.gateway.EqualizerGateway
import dev.olog.core.prefs.EqualizerPreferencesGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.equalizer.audioeffect.NormalizedEqualizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class EqualizerImpl @Inject constructor(
    gateway: EqualizerGateway,
    prefs: EqualizerPreferencesGateway,
    schedulers: Schedulers

) : AbsEqualizer(gateway, prefs, schedulers),
    IEqualizerInternal,
    CoroutineScope by MainScope() {

    companion object {
        private const val BANDS = 5
        private const val BAND_LIMIT = 15f
    }

    private var equalizer: NormalizedEqualizer? = null

    private var isImplementedByDevice = false

    init {
        for (queryEffect in AudioEffect.queryEffects()) {
            if (queryEffect.type == AudioEffect.EFFECT_TYPE_EQUALIZER){
                isImplementedByDevice = true
            }
        }
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        if (!isImplementedByDevice){
            return
        }
        launch {
            release()
            try {
                equalizer = NormalizedEqualizer(0, audioSessionId).apply {
                    enabled = prefs.isEqualizerEnabled()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }

    private fun release() {
        safeAction {
            equalizer?.release()
            equalizer = null
        }
    }

    override fun onDestroy() {
        release()
    }

    override fun setEnabled(enabled: Boolean) {
        safeAction {
            equalizer?.enabled = enabled
        }
        prefs.setEqualizerEnabled(enabled)
    }

    override suspend fun setCurrentPreset(preset: EqualizerPreset) {
        updateCurrentPresetIfCustom()
        prefs.setCurrentPresetId(preset.id)
        safeAction {
            equalizer?.let {
                updatePresetInternal(preset)
            }
        }
    }

    override fun getBandCount(): Int = BANDS

    override fun getBandLevel(band: Int): Float {
        if (!isImplementedByDevice){
            return 0f
        }
        try {
            return equalizer?.getBandLevel(band) ?: 0f
        } catch (ex: IllegalStateException){
            ex.printStackTrace()
            // throws getParameter() called on uninitialized AudioEffect.
            return 0f
        }
    }

    override fun setBandLevel(band: Int, level: Float) {
        safeAction {
            equalizer?.setBandLevel(band, level)
        }
    }


    override fun getBandLimit(): Float =
        BAND_LIMIT

    override fun getAllBandsCurrentLevel(): List<EqualizerBand> {
        if (!isImplementedByDevice){
            return emptyList()
        }

        val result = mutableListOf<EqualizerBand>()
        for (bandIndex in 0 until BANDS) {
            val gain = equalizer!!.getBandLevel(bandIndex)
            val frequency = equalizer!!.getBandFrequency(bandIndex)
            result.add(EqualizerBand(gain, frequency))
        }
        return result
    }

    private fun updatePresetInternal(preset: EqualizerPreset) {
        safeAction {
            preset.bands.forEachIndexed { index, equalizerBand ->
                setBandLevel(index, equalizerBand.gain)
            }
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