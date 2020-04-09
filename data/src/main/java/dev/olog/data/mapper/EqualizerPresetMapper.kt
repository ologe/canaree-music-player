package dev.olog.data.mapper

import dev.olog.domain.entity.EqualizerBand
import dev.olog.domain.entity.EqualizerPreset
import dev.olog.data.model.db.EqualizerBandEntity
import dev.olog.data.model.db.EqualizerPresetEntity

internal fun EqualizerPresetEntity.toDomain(): EqualizerPreset {
    return EqualizerPreset(
        id = id,
        name = name,
        bands = bands.map {
            EqualizerBand(gain = it.gain, frequency = it.frequency)
        },
        isCustom = isCustom
    )
}

internal fun EqualizerPreset.toEntity(): EqualizerPresetEntity {
    return EqualizerPresetEntity(
        id = id,
        name = name,
        bands = bands.map {
            EqualizerBandEntity(gain = it.gain, frequency = it.frequency)
        },
        isCustom = isCustom
    )
}