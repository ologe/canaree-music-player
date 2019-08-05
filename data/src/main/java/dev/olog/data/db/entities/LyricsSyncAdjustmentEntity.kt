package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lyrics_sync_adjustment",
    indices = [(Index("id"))]
)
class LyricsSyncAdjustmentEntity(
    @PrimaryKey
    @JvmField
    val id: Long,
    @JvmField
    val millis: Long
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LyricsSyncAdjustmentEntity

        if (id != other.id) return false
        if (millis != other.millis) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + millis.hashCode()
        return result
    }
}