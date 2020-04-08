package dev.olog.lib.equalizer.equalizer

import dev.olog.domain.entity.EqualizerBand
import dev.olog.domain.entity.EqualizerPreset
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

    fun onAudioSessionIdChanged(callerHash: Int, audioSessionId: Int)
    fun onDestroy(callerHash: Int)

    fun setEnabled(enabled: Boolean)

    fun getPresets(): List<EqualizerPreset>
    fun observeCurrentPreset(): Flow<EqualizerPreset>
    fun getCurrentPreset(): EqualizerPreset
    suspend fun setCurrentPreset(preset: EqualizerPreset)
    suspend fun updateCurrentPresetIfCustom()

    fun getBandCount(): Int
    fun getBandLevel(band: Int): Float
    fun getAllBandsCurrentLevel(): List<EqualizerBand>
    fun setBandLevel(band: Int, level: Float)
    fun getBandLimit(): Float

}