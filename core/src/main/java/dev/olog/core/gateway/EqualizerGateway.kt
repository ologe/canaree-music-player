package dev.olog.core.gateway

import dev.olog.core.entity.EqualizerBand
import dev.olog.core.entity.EqualizerPreset
import kotlinx.coroutines.flow.Flow

interface EqualizerGateway {

    fun getPresets(): List<EqualizerPreset>
    fun getCurrentPreset(): EqualizerPreset
    fun observeCurrentPreset(): Flow<EqualizerPreset>

    suspend fun addPreset(title: String, bands: List<EqualizerBand>)
    suspend fun updatePreset(preset: EqualizerPreset)
    suspend fun deletePreset(id: Long)

}