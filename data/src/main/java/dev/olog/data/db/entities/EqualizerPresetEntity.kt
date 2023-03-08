package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


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

@Serializable
data class EqualizerBandEntity(
    val gain: Float,
    val frequency: Float
)