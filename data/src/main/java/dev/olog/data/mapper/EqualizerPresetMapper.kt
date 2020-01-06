package dev.olog.data.mapper

import dev.olog.core.entity.EqualizerBand
import dev.olog.core.entity.EqualizerPreset
import dev.olog.data.model.db.EqualizerBandEntity
import dev.olog.data.model.db.EqualizerPresetEntity

internal fun EqualizerPresetEntity.toDomain(): EqualizerPreset {
    return EqualizerPreset(
        id,
        name,
        bands.map { EqualizerBand(it.gain, it.frequency) },
        isCustom
    )
}

internal fun EqualizerPreset.toEntity(): EqualizerPresetEntity {
    return EqualizerPresetEntity(
        id,
        name,
        bands.map {
            EqualizerBandEntity(
                it.gain,
                it.frequency
            )
        },
        isCustom
    )
}