package dev.olog.lib.repository

import android.os.Build
import dev.olog.lib.model.db.EqualizerBandEntity
import dev.olog.lib.model.db.EqualizerPresetEntity

internal object EqualizerDefaultPresets {

    fun createDefaultPresets(): List<EqualizerPresetEntity> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return createDefaultPresetsApi28()
        }
        return createDefaultPresetsPre28()
    }

    private fun createDefaultPresetsApi28(): List<EqualizerPresetEntity> {
        var id = 0L
        return listOf(
            // itunes presets
            createPresetApi28(
                id++, "Flat",
                0f, 0f, 0f, 0f, 0f,
                0f, 0f, 0f, 0f, 0f
            ),
            createPresetApi28(
                id++, "Acoustic",
                5f, 5f, 4f, 1f, 1.5f,
                1.5f, 3.5f, 4f, 3.5f, 2f
            ),
            createPresetApi28(
                id++, "Bass Booster",
                5.5f, 4.5f, 4f, 2.75f, 1.5f,
                0f, 0f, 0f, 0f, 0f
            ),
            createPresetApi28(
                id++, "Bass Reducer",
                -5.5f, -4.5f, -4f, -2.75f, -1.5f,
                0f, 0f, 0f, 0f, 0f
            ),
            createPresetApi28(
                id++, "Classical",
                4.5f, 4f, 3f, 2.5f, -1.5f,
                -1.5f, 0f, 2.5f, 3.5f, 4f
            ),
            createPresetApi28(
                id++, "Deep",
                4.5f, 3.5f, 2f, 1f, 3f,
                2.5f, 1.5f, -2f, -3.5f, -4.5f
            ),
            createPresetApi28(
                id++, "Electronic",
                4.5f, 4f, 1.5f, 0f, -1.8f,
                2f, 1f, 1.5f, 4f, 5f
            ),
            createPresetApi28(
                id++, "Hip-Hop",
                5f, 4f, 1.5f, 3f, -1f,
                -1f, 1.5f, -.5f, 2f, 3f
            ),
            createPresetApi28(
                id++, "Jazz",
                4f, 3f, 1.5f, 2f, -1.5f,
                -1.5f, 0f, 1.5f, 3f, 4f
            ),
            createPresetApi28(
                id++, "Latin",
                4.5f, 3f, 0f, 0f, -1.5f,
                -1.5f, -1.5f, 0f, 3f, 4.5f
            ),
            createPresetApi28(
                id++, "Loudness",
                6f, 4f, 0f, 0f, -2f,
                0f, -1f, -4.5f, 5f, 1f
            ),
            createPresetApi28(
                id++, "Lounge",
                -3f, -1.5f, -.5f, 1.5f, 4f,
                2.5f, 0f, -1.5f, 2f, 1f
            ),
            createPresetApi28(
                id++, "Piano",
                3f, 2f, 0f, 2f, 3f,
                1.5f, 4f, 3f, 3f, 4f
            ),
            createPresetApi28(
                id++, "Pop",
                -1.5f, -1f, 0f, 2f, 4f,
                4f, 2f, 0f, -1f, -1.2f
            ),
            createPresetApi28(
                id++, "R&B",
                3f, 7.2f, 6f, 1.5f, -2f,
                -1.5f, 2f, 2.5f, 2.5f, 3.5f
            ),
            createPresetApi28(
                id++, "Rock",
                4.8f, 4.2f, 3f, 1.5f, -.5f,
                -1f, .5f, 2.5f, 3.5f, 4.5f
            ),
            createPresetApi28(
                id++, "Small Speakers",
                5f, 4.5f, 4f, 1f, 2f,
                1.5f, 3.5f, 4f, 3.5f, 2f
            ),
            createPresetApi28(
                id++, "Spoken Word",
                -3.8f, -.5f, 0f, .8f, 3.5f,
                4.5f, 4.5f, 4.5f, 2.8f, 0f
            ),
            createPresetApi28(
                id++, "Treble Booster",
                0f, 0f, 0f, 0f, 0f,
                1.5f, 2f, 4f, 4.5f, 5.2f
            ),
            createPresetApi28(
                id, "Treble Reducer",
                0f, 0f, 0f, 0f, 0f,
                -1.5f, -2f, -4f, -4.5f, -5.2f
            )
        )
    }


    private fun createDefaultPresetsPre28(): List<EqualizerPresetEntity> {
        var id = 0L
        return listOf(
            createPreset(
                id++, "Normal",
                3f, 0f, 0f, 0f, 3f
            ),
            createPreset(
                id++, "Classical",
                5f, 3f, -2f, 4f, 4f
            ),
            createPreset(
                id++, "Flat",
                0f, 0f, 0f, 0f, 0f
            ),
            createPreset(
                id++, "Folk",
                3f, 0f, 0f, 2f, -1f
            ),
            createPreset(
                id++, "Heavy Metal",
                4f, 1f, 9f, 3f, 0f
            ),
            createPreset(
                id++, "Hip Hop",
                5f, 3f, 0f, 1f, 3f
            ),
            createPreset(
                id++, "Jazz",
                4f, 2f, -2f, 2f, 5f
            ),
            createPreset(
                id++, "Pop",
                -1f, 2f, 5f, 1f, -2f
            ),
            createPreset(
                id, "Rock",
                5f, 3f, -1f, 3f, 5f
            )
        )
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

    private fun createPreset(
        id: Long,
        title: String,
        band60: Float,
        band230: Float,
        band910: Float,
        band3600: Float,
        band14000: Float
    ): EqualizerPresetEntity {
        return EqualizerPresetEntity(
            id, title, listOf(
                EqualizerBandEntity(band60, 60f),
                EqualizerBandEntity(band230, 230f),
                EqualizerBandEntity(band910, 910f),
                EqualizerBandEntity(band3600, 3600f),
                EqualizerBandEntity(band14000, 14000f)
            ), false
        )
    }


}