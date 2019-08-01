package dev.olog.data.repository

import android.os.Build
import dev.olog.core.entity.EqualizerBand
import dev.olog.core.entity.EqualizerPreset
import dev.olog.core.gateway.EqualizerGateway
import dev.olog.core.prefs.EqualizerPreferencesGateway
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.db.entities.EqualizerBandEntity
import dev.olog.data.db.entities.EqualizerPresetEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.switchMap
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class EqualizerRepository @Inject constructor(
    appDatabase: AppDatabase,
    private val prefs: EqualizerPreferencesGateway

) : EqualizerGateway {

    private val dao = appDatabase.equalizerPresetsDao()

    init {
        GlobalScope.launch(Dispatchers.IO) {
            if (dao.getPresets().isEmpty()) {
                // called only first time
                val presets = createDefaultPresets()
                GlobalScope.launch { dao.insertPresets(presets) }
            }
        }
    }

    override fun getPresets(): List<EqualizerPreset> {
        return dao.getPresets().map { it.toDomain() }
    }

    override fun getCurrentPreset(): EqualizerPreset {
        val currentPresetId = prefs.getCurrentPresetId()
        return dao.getPresetById(currentPresetId).toDomain()
    }

    override fun observeCurrentPreset(): Flow<EqualizerPreset> {
        return prefs.observeCurrentPresetId()
            .switchMap { dao.observePresetById(it).asFlow() }
            .map { it.toDomain() }
            .distinctUntilChanged()
    }

    override fun saveCurrentPreset(preset: EqualizerPreset) {
        val newId = getPresets().maxBy { it.id }!!.id + 1
        saveCurrentPreset(preset.copy(id = newId))
        prefs.setCurrentPresetId(newId)
    }

    override suspend fun addPreset(preset: EqualizerPreset) {
        dao.insertPreset(preset.toEntity())
    }

    override suspend fun deletePreset(preset: EqualizerPreset) {
        dao.deletePreset(preset.toEntity())
    }

    private fun EqualizerPresetEntity.toDomain(): EqualizerPreset {
        return EqualizerPreset(
            id,
            name,
            bands.map { EqualizerBand(it.gain, it.frequency) },
            isCustom
        )
    }

    private fun EqualizerPreset.toEntity(): EqualizerPresetEntity {
        return EqualizerPresetEntity(
            id,
            name,
            bands.map { EqualizerBandEntity(it.gain, it.frequency) },
            isCustom
        )
    }

    private fun createDefaultPresets(): List<EqualizerPresetEntity> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return createDefaultPresetsApi28()
        }
        return createDefaultPresetsPre28()
    }

    private fun createDefaultPresetsApi28(): List<EqualizerPresetEntity> {
        return listOf(
            // itunes presets
            createPresetApi28(0, "Acoustic",
                5f, 5f, 4f, 1f, 1.5f,
                1.5f, 3.5f, 4f, 3.5f, 2f
            ),
            createPresetApi28(1, "Bass Booster",
                5.5f, 4.5f, 4f, 2.75f, 1.5f,
                0f, 0f, 0f, 0f, 0f
            ),
            createPresetApi28(3, "Bass Reducer",
                -5.5f, -4.5f, -4f, -2.75f, -1.5f,
                0f, 0f, 0f, 0f, 0f
            ),
            createPresetApi28(4, "Classical",
                4.5f, 4f, 3f, 2.5f, -1.5f,
                -1.5f, 0f, 2.5f, 3.5f, 4f
            ),
            createPresetApi28(5, "Dance",
                4f, 7f, 5f, 0f, 2.5f,
                3.5f, 5f, 4.5f, 3.5f, 0f
            ),
            createPresetApi28(6, "Deep",
                4.5f, 3.5f, 2f, 1f, 3f,
                2.5f, 1.5f, -2f, -3.5f, -4.5f
            ),
            createPresetApi28(7, "Electronic",
                4.5f, 4f, 1.5f, 0f, -1.8f,
                2f, 1f, 1.5f, 4f, 5f
            ),
            createPresetApi28(8, "Flat",
                0f, 0f, 0f, 0f, 0f,
                0f, 0f, 0f, 0f, 0f
            ),
            createPresetApi28(9, "Hip-Hop",
                5f, 4f, 1.5f, 3f, -1f,
                -1f, 1.5f, -.5f, 2f, 3f
            ),
            createPresetApi28(10, "Jazz",
                4f, 3f, 1.5f, 2f, -1.5f,
                -1.5f, 0f, 1.5f, 3f, 4f
            ),
            createPresetApi28(11, "Latin",
                4.5f, 3f, 0f, 0f, -1.5f,
                -1.5f, -1.5f, 0f, 3f, 4.5f
            ),
            createPresetApi28(12, "Loudness",
                6f, 4f, 0f, 0f, -2f,
                0f, -1f, -4.5f, 5f, 1f
            ),
            createPresetApi28(13, "Lounge",
                -3f, -1.5f, -.5f, 1.5f, 4f,
                2.5f, 0f, -1.5f, 2f, 1f
            ),
            createPresetApi28(14, "Piano",
                3f, 2f, 0f, 2f, 3f,
                1.5f, 4f, 3f, 3f, 4f
            ),
            createPresetApi28(15, "Pop",
                -1.5f, -1f, 0f, 2f, 4f,
                4f, 2f, 0f, -1f, -1.2f
            ),
            createPresetApi28(16, "R&B",
                3f, 7.2f, 6f, 1.5f, -2f,
                -1.5f, 2f, 2.5f, 2.5f, 3.5f
            ),
            createPresetApi28(17, "Rock",
                4.8f, 4.2f, 3f, 1.5f, -.5f,
                -1f, .5f, 2.5f, 3.5f, 4.5f
            ),
            createPresetApi28(18, "Small Speakers",
                5f, 4.5f, 4f, 1f, 2f,
                1.5f, 3.5f, 4f, 3.5f, 2f
            ),
            createPresetApi28(19, "Spoken Word",
                -3.8f, -.5f, 0f, .8f, 3.5f,
                4.5f, 4.5f, 4.5f, 2.8f, 0f
            ),
            createPresetApi28(20, "Treble Booster",
                0f, 0f, 0f, 0f, 0f,
                1.5f, 2f, 4f, 4.5f, 5.2f
            ),
            createPresetApi28(21, "Treble Reducer",
                0f, 0f, 0f, 0f, 0f,
                -1.5f, -2f, -4f, -4.5f, -5.2f
            )
        )
    }

    private fun createDefaultPresetsPre28(): List<EqualizerPresetEntity> {
        return listOf()
    }

    private fun createPresetApi28(
        id: Long,
        title: String,
        band32: Float,
        band64: Float,
        band125: Float,
        band250: Float,
        band500: Float,
        band1k: Float,
        band2k: Float,
        band4k: Float,
        band8k: Float,
        band16k: Float
    ): EqualizerPresetEntity {
        return EqualizerPresetEntity(
            id, title, listOf(
                EqualizerBandEntity(band32, 32f),
                EqualizerBandEntity(band64, 64f),
                EqualizerBandEntity(band125, 125f),
                EqualizerBandEntity(band250, 250f),
                EqualizerBandEntity(band500, 500f),
                EqualizerBandEntity(band1k, 1000f),
                EqualizerBandEntity(band2k, 2000f),
                EqualizerBandEntity(band4k, 4000f),
                EqualizerBandEntity(band8k, 8000f),
                EqualizerBandEntity(band16k, 16000f)
            ), false
        )
    }

}