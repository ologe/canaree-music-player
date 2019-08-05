package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "equalizer_preset",
    indices = [Index("id")]
)
class EqualizerPresetEntity(
    @PrimaryKey
    @JvmField
    val id: Long,
    @JvmField
    val name: String,
    @JvmField
    val bands: List<EqualizerBandEntity>,
    @JvmField
    val isCustom: Boolean
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EqualizerPresetEntity

        if (id != other.id) return false
        if (name != other.name) return false
        if (bands != other.bands) return false
        if (isCustom != other.isCustom) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + bands.hashCode()
        result = 31 * result + isCustom.hashCode()
        return result
    }
    fun withId(id: Long): EqualizerPresetEntity {
        return EqualizerPresetEntity(
            id = id,
            name = name,
            bands = bands,
            isCustom = isCustom
        )
    }

}

class EqualizerBandEntity(
    @JvmField
    val gain: Float,
    @JvmField
    val frequency: Float
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EqualizerBandEntity

        if (gain != other.gain) return false
        if (frequency != other.frequency) return false

        return true
    }

    override fun hashCode(): Int {
        var result = gain.hashCode()
        result = 31 * result + frequency.hashCode()
        return result
    }
}

