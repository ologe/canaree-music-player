package dev.olog.data.local.equalizer.preset

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "equalizer_preset",
    indices = [Index("id")]
)
data class EqualizerPresetEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val bands: List<EqualizerBandEntity>,
    val isCustom: Boolean
)

data class EqualizerBandEntity(
    val gain: Float,
    val frequency: Float
)