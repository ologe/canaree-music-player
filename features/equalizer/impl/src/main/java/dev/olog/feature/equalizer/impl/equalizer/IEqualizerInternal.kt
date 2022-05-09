package dev.olog.feature.equalizer.impl.equalizer

import dev.olog.feature.equalizer.model.EqualizerBand
import dev.olog.feature.equalizer.model.EqualizerPreset
import kotlinx.coroutines.flow.Flow

interface IEqualizerInternal {

    fun onAudioSessionIdChanged(audioSessionId: Int)
    fun onDestroy()
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