package dev.olog.equalizer

import dev.olog.core.entity.EqualizerBand
import dev.olog.core.entity.EqualizerPreset
import kotlinx.coroutines.flow.Flow

/**
 * - presets are created manually, there are different on pre android P and after
 * - band level has to be saved from -15 to 15 decibel
 *
 * - on pre android P is using Equalizer.java
 *  - 5 bands
 * - on anroid P is using DynamicsProcessing.java
 *  -10 bands
 */
interface IEqualizer {

    fun onAudioSessionIdChanged(audioSessionId: Int)
    fun onDestroy()
    fun setEnabled(enabled: Boolean)

    fun getPresets(): List<EqualizerPreset>
    fun observeCurrentPreset(): Flow<EqualizerPreset>
    fun getCurrentPreset(): EqualizerPreset
    fun setCurrentPreset(preset: EqualizerPreset)

    fun getBandCount(): Int
    fun getBandLevel(band: Int): Float
    fun getAllBandsLevel(): List<EqualizerBand>
    fun setBandLevel(band: Int, level: Float)
    fun getBandLimit(): Float

}