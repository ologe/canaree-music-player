package dev.olog.data.equalizer

import dev.olog.core.equalizer.EqualizerBand
import dev.olog.core.equalizer.EqualizerPreset

internal fun EqualizerQueries.selectPresets(
    query: () -> List<Equalizer_preset>
): List<EqualizerPreset> {
    return query()
        .map { preset ->
            val values = selectPresetValues(preset.id).executeAsList()
            EqualizerPreset(
                id = preset.id,
                name = preset.name,
                isCustom = preset.custom,
                bands = values.map {
                    EqualizerBand(
                        id = it.id,
                        gain = it.gain,
                        frequency = it.frequency,
                    )
                },
            )
        }
}