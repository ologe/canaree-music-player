package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "podcast_position")
class PodcastPositionEntity(
    @PrimaryKey(autoGenerate = false)
    @JvmField
    val id: Long,
    @JvmField
    val position: Long
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PodcastPositionEntity

        if (id != other.id) return false
        if (position != other.position) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + position.hashCode()
        return result
    }
}