package dev.olog.data.equalizer

import dev.olog.core.equalizer.EqualizerBand
import dev.olog.core.equalizer.EqualizerPreset

object EqualizerTestModels {

    private var presetValueId = 1L

    val createDefaultPresetsApi28: List<EqualizerPreset> = listOf(
        // itunes presets
        createPresetApi28(
            id = 1,
            title = "Flat",
            band32 = 0f,
            band64 = 0f,
            band125 = 0f,
            band250 = 0f,
            band500 = 0f,
            band1k = 0f,
            band2k = 0f,
            band4k = 0f,
            band8k = 0f,
            band16k = 0f
        ),
        createPresetApi28(
            id = 2,
            title = "Acoustic",
            band32 = 5f,
            band64 = 5f,
            band125 = 4f,
            band250 = 1f,
            band500 = 1.5f,
            band1k = 1.5f,
            band2k = 3.5f,
            band4k = 4f,
            band8k = 3.5f,
            band16k = 2f
        ),
        createPresetApi28(
            id = 3,
            title = "Bass Booster",
            band32 = 5.5f,
            band64 = 4.5f,
            band125 = 4f,
            band250 = 2.75f,
            band500 = 1.5f,
            band1k = 0f,
            band2k = 0f,
            band4k = 0f,
            band8k = 0f,
            band16k = 0f
        ),
        createPresetApi28(
            id = 4,
            title = "Bass Reducer",
            band32 = -5.5f,
            band64 = -4.5f,
            band125 = -4f,
            band250 = -2.75f,
            band500 = -1.5f,
            band1k = 0f,
            band2k = 0f,
            band4k = 0f,
            band8k = 0f,
            band16k = 0f
        ),
        createPresetApi28(
            id = 5,
            title = "Classical",
            band32 = 4.5f,
            band64 = 4f,
            band125 = 3f,
            band250 = 2.5f,
            band500 = -1.5f,
            band1k = -1.5f,
            band2k = 0f,
            band4k = 2.5f,
            band8k = 3.5f,
            band16k = 4f
        ),
        createPresetApi28(
            id = 6,
            title = "Deep",
            band32 = 4.5f,
            band64 = 3.5f,
            band125 = 2f,
            band250 = 1f,
            band500 = 3f,
            band1k = 2.5f,
            band2k = 1.5f,
            band4k = -2f,
            band8k = -3.5f,
            band16k = -4.5f
        ),
        createPresetApi28(
            id = 7,
            title = "Electronic",
            band32 = 4.5f,
            band64 = 4f,
            band125 = 1.5f,
            band250 = 0f,
            band500 = -1.8f,
            band1k = 2f,
            band2k = 1f,
            band4k = 1.5f,
            band8k = 4f,
            band16k = 5f
        ),
        createPresetApi28(
            id = 8,
            title = "Hip-Hop",
            band32 = 5f,
            band64 = 4f,
            band125 = 1.5f,
            band250 = 3f,
            band500 = -1f,
            band1k = -1f,
            band2k = 1.5f,
            band4k = -.5f,
            band8k = 2f,
            band16k = 3f
        ),
        createPresetApi28(
            id = 9,
            title = "Jazz",
            band32 = 4f,
            band64 = 3f,
            band125 = 1.5f,
            band250 = 2f,
            band500 = -1.5f,
            band1k = -1.5f,
            band2k = 0f,
            band4k = 1.5f,
            band8k = 3f,
            band16k = 4f
        ),
        createPresetApi28(
            id = 10,
            title = "Latin",
            band32 = 4.5f,
            band64 = 3f,
            band125 = 0f,
            band250 = 0f,
            band500 = -1.5f,
            band1k = -1.5f,
            band2k = -1.5f,
            band4k = 0f,
            band8k = 3f,
            band16k = 4.5f
        ),
        createPresetApi28(
            id = 11,
            title = "Loudness",
            band32 = 6f,
            band64 = 4f,
            band125 = 0f,
            band250 = 0f,
            band500 = -2f,
            band1k = 0f,
            band2k = -1f,
            band4k = -4.5f,
            band8k = 5f,
            band16k = 1f
        ),
        createPresetApi28(
            id = 12,
            title = "Lounge",
            band32 = -3f,
            band64 = -1.5f,
            band125 = -.5f,
            band250 = 1.5f,
            band500 = 4f,
            band1k = 2.5f,
            band2k = 0f,
            band4k = -1.5f,
            band8k = 2f,
            band16k = 1f
        ),
        createPresetApi28(
            id = 13,
            title = "Piano",
            band32 = 3f,
            band64 = 2f,
            band125 = 0f,
            band250 = 2f,
            band500 = 3f,
            band1k = 1.5f,
            band2k = 4f,
            band4k = 3f,
            band8k = 3f,
            band16k = 4f
        ),
        createPresetApi28(
            id = 14,
            title = "Pop",
            band32 = -1.5f,
            band64 = -1f,
            band125 = 0f,
            band250 = 2f,
            band500 = 4f,
            band1k = 4f,
            band2k = 2f,
            band4k = 0f,
            band8k = -1f,
            band16k = -1.2f
        ),
        createPresetApi28(
            id = 15,
            title = "R&B",
            band32 = 3f,
            band64 = 7.2f,
            band125 = 6f,
            band250 = 1.5f,
            band500 = -2f,
            band1k = -1.5f,
            band2k = 2f,
            band4k = 2.5f,
            band8k = 2.5f,
            band16k = 3.5f
        ),
        createPresetApi28(
            id = 16,
            title = "Rock",
            band32 = 4.8f,
            band64 = 4.2f,
            band125 = 3f,
            band250 = 1.5f,
            band500 = -.5f,
            band1k = -1f,
            band2k = .5f,
            band4k = 2.5f,
            band8k = 3.5f,
            band16k = 4.5f
        ),
        createPresetApi28(
            id = 17,
            title = "Small Speakers",
            band32 = 5f,
            band64 = 4.5f,
            band125 = 4f,
            band250 = 1f,
            band500 = 2f,
            band1k = 1.5f,
            band2k = 3.5f,
            band4k = 4f,
            band8k = 3.5f,
            band16k = 2f
        ),
        createPresetApi28(
            id = 18,
            title = "Spoken Word",
            band32 = -3.8f,
            band64 = -.5f,
            band125 = 0f,
            band250 = .8f,
            band500 = 3.5f,
            band1k = 4.5f,
            band2k = 4.5f,
            band4k = 4.5f,
            band8k = 2.8f,
            band16k = 0f
        ),
        createPresetApi28(
            id = 19,
            title = "Treble Booster",
            band32 = 0f,
            band64 = 0f,
            band125 = 0f,
            band250 = 0f,
            band500 = 0f,
            band1k = 1.5f,
            band2k = 2f,
            band4k = 4f,
            band8k = 4.5f,
            band16k = 5.2f
        ),
        createPresetApi28(
            id = 20,
            title = "Treble Reducer",
            band32 = 0f,
            band64 = 0f,
            band125 = 0f,
            band250 = 0f,
            band500 = 0f,
            band1k = -1.5f,
            band2k = -2f,
            band4k = -4f,
            band8k = -4.5f,
            band16k = -5.2f
        )
    )

    val createDefaultPresetsPreApi28: List<EqualizerPreset> =  listOf(
        createPreset(
            id = 21,
            title = "Normal",
            band60 = 3f,
            band230 = 0f,
            band910 = 0f,
            band3600 = 0f,
            band14000 = 3f
        ),
        createPreset(
            id = 22,
            title = "Classical",
            band60 = 5f,
            band230 = 3f,
            band910 = -2f,
            band3600 = 4f,
            band14000 = 4f
        ),
        createPreset(
            id = 23,
            title = "Flat",
            band60 = 0f,
            band230 = 0f,
            band910 = 0f,
            band3600 = 0f,
            band14000 = 0f
        ),
        createPreset(
            id = 24,
            title = "Folk",
            band60 = 3f,
            band230 = 0f,
            band910 = 0f,
            band3600 = 2f,
            band14000 = -1f
        ),
        createPreset(
            id = 25,
            title = "Heavy Metal",
            band60 = 4f,
            band230 = 1f,
            band910 = 9f,
            band3600 = 3f,
            band14000 = 0f
        ),
        createPreset(
            id = 26,
            title = "Hip Hop",
            band60 = 5f,
            band230 = 3f,
            band910 = 0f,
            band3600 = 1f,
            band14000 = 3f
        ),
        createPreset(
            id = 27,
            title = "Jazz",
            band60 = 4f,
            band230 = 2f,
            band910 = -2f,
            band3600 = 2f,
            band14000 = 5f
        ),
        createPreset(
            id = 28,
            title = "Pop",
            band60 = -1f,
            band230 = 2f,
            band910 = 5f,
            band3600 = 1f,
            band14000 = -2f
        ),
        createPreset(
            id = 29,
            title = "Rock",
            band60 = 5f,
            band230 = 3f,
            band910 = -1f,
            band3600 = 3f,
            band14000 = 5f
        )
    )

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
    ): EqualizerPreset {
        return EqualizerPreset(
            id = id,
            name = title,
            bands = listOf(
                EqualizerBand(id = presetValueId++, gain = band32, frequency = 32f),
                EqualizerBand(id = presetValueId++, gain = band64, frequency = 64f),
                EqualizerBand(id = presetValueId++, gain = band125, frequency = 125f),
                EqualizerBand(id = presetValueId++, gain = band250, frequency = 250f),
                EqualizerBand(id = presetValueId++, gain = band500, frequency = 500f),
                EqualizerBand(id = presetValueId++, gain = band1k, frequency = 1000f),
                EqualizerBand(id = presetValueId++, gain = band2k, frequency = 2000f),
                EqualizerBand(id = presetValueId++, gain = band4k, frequency = 4000f),
                EqualizerBand(id = presetValueId++, gain = band8k, frequency = 8000f),
                EqualizerBand(id = presetValueId++, gain = band16k, frequency = 16000f)
            ),
            isCustom = false
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
    ): EqualizerPreset {
        return EqualizerPreset(
            id = id,
            name = title,
            bands = listOf(
                EqualizerBand(id = presetValueId++, gain = band60, frequency = 60f),
                EqualizerBand(id = presetValueId++, gain = band230, frequency = 230f),
                EqualizerBand(id = presetValueId++, gain = band910, frequency = 910f),
                EqualizerBand(id = presetValueId++, gain = band3600, frequency = 3600f),
                EqualizerBand(id = presetValueId++, gain = band14000, frequency = 14000f)
            ),
            isCustom = false
        )
    }

}