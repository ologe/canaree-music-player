package dev.olog.data.equalizer

import dev.olog.core.equalizer.EqualizerBand
import dev.olog.core.equalizer.EqualizerPreset
import dev.olog.core.equalizer.EqualizerGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.feature.equalizer.EqualizerPrefs
import dev.olog.shared.android.BuildVersion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class EqualizerRepository @Inject constructor(
    private val schedulers: Schedulers,
    private val queries: EqualizerQueries,
    private val prefs: EqualizerPrefs,
    private val buildVersion: BuildVersion,
) : EqualizerGateway {

    override fun getPresets(): List<EqualizerPreset> {
        val defaultPresets = if (buildVersion.isPie()) {
            queries.selectDefaultPresetsApi28()
        } else {
            queries.selectDefaultPresetsPreApi28()
        }
        return queries.selectPresets {
            defaultPresets.executeAsList() +
            queries.selectCustomPresets().executeAsList()
        }
    }

    override fun getCurrentPreset(): EqualizerPreset {
        // TODO handle when can't find preset, remember below api 28 start from 20
        val currentPresetId = prefs.currentPresetId.get()
        val presetValues = queries.selectPresetValues(currentPresetId).executeAsList()
        val preset =  queries.selectPresetById(currentPresetId).executeAsOne()
        return preset.toDomain(presetValues)
    }

    override fun observeCurrentPreset(): Flow<EqualizerPreset> {
        return prefs.currentPresetId.observe()
            .mapLatest { id ->
                val presetValues = queries.selectPresetValues(id).executeAsList()
                queries.selectPresetById(id).executeAsOne().toDomain(presetValues)
            }.distinctUntilChanged()
            .flowOn(schedulers.io)
    }

    override suspend fun addPreset(
        title: String,
        bands: List<EqualizerBand>
    ) = withContext(schedulers.io) {
        queries.transaction {
            queries.insertPreset(title)
            val presetId = queries.selectLastInsertedRowId().executeAsOne()
            for (band in bands) {
                queries.insertPresetValue(
                    preset_id = presetId,
                    gain = band.gain,
                    frequency = band.frequency
                )
            }
        }
    }

    override suspend fun updatePreset(
        preset: EqualizerPreset
    ) = withContext(schedulers.io) {
        require(preset.isCustom)

        queries.transaction {
            queries.updatePreset(name = preset.name, id = preset.id)

            for (band in preset.bands) {
                queries.updatePresetValue(
                    gain = band.gain,
                    frequency = band.frequency,
                    id = band.id,
                )
            }
        }
    }

    override suspend fun deletePreset(id: Long) = withContext(schedulers.io) {
        queries.transaction {
            queries.deletePresetValues(id)
            queries.deletePreset(id)
        }
    }

}