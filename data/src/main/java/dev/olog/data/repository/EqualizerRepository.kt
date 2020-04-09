package dev.olog.data.repository

import dev.olog.domain.entity.EqualizerPreset
import dev.olog.domain.gateway.EqualizerGateway
import dev.olog.domain.prefs.EqualizerPreferencesGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.data.db.EqualizerPresetsDao
import dev.olog.data.mapper.toDomain
import dev.olog.data.mapper.toEntity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class EqualizerRepository @Inject constructor(
    private val equalizerDao: EqualizerPresetsDao,
    private val prefs: EqualizerPreferencesGateway,
    private val schedulers: Schedulers

) : EqualizerGateway {

    init {
        GlobalScope.launch(schedulers.io) {
            if (equalizerDao.getPresets().isEmpty()) {
                // called only first time
                val presets = EqualizerDefaultPresets.createDefaultPresets()
                equalizerDao.insertPresets(*presets.toTypedArray())
            }
        }
    }

    override fun getPresets(): List<EqualizerPreset> {
        return equalizerDao.getPresets().map { it.toDomain() }
    }

    override fun getCurrentPreset(): EqualizerPreset {
        val currentPresetId = prefs.getCurrentPresetId()
        return equalizerDao.getPresetById(currentPresetId)!!.toDomain()
    }

    override fun observeCurrentPreset(): Flow<EqualizerPreset> {
        return prefs.observeCurrentPresetId()
            .flatMapLatest { equalizerDao.observePresetById(it) }
            .map { it.toDomain() }
            .distinctUntilChanged()
    }

    override suspend fun addPreset(preset: EqualizerPreset) {
        require(preset.id == -1L)
        require(preset.isCustom)

        val newId = getPresets().maxBy { it.id }!!.id + 1
        equalizerDao.insertPresets(preset.toEntity().copy(id = newId))
    }

    override suspend fun updatePreset(preset: EqualizerPreset) {
        equalizerDao.insertPresets(preset.toEntity())
    }

    override suspend fun deletePreset(preset: EqualizerPreset) {
        equalizerDao.deletePreset(preset.toEntity())
    }

}