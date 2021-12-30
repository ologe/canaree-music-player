package dev.olog.data.equalizer

import dev.olog.core.equalizer.EqualizerBand
import dev.olog.core.equalizer.EqualizerPreset

internal fun Equalizer_preset.toDomain(bands: List<Equalizer_preset_value>): EqualizerPreset {
    return EqualizerPreset(
        id = id,
        name = name,
        isCustom = custom,
        bands = bands.map {
            EqualizerBand(
                id = it.id,
                gain = it.gain,
                frequency = it.frequency
            )
        }
    )
}