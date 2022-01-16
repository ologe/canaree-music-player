package dev.olog.core.equalizer

import kotlinx.coroutines.flow.Flow

interface EqualizerGateway {

    fun getPresets(): List<EqualizerPreset>
    fun getCurrentPreset(): EqualizerPreset
    fun observeCurrentPreset(): Flow<EqualizerPreset>

    suspend fun addPreset(title: String, bands: List<EqualizerBand>)
    suspend fun updatePreset(preset: EqualizerPreset)
    suspend fun deletePreset(id: Long)

}