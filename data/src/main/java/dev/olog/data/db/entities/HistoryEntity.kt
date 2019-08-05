package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "song_history",
    indices = [(Index("id"))]
)
class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @JvmField
    val id: Int = 0,
    @JvmField
    val songId: Long,
    @JvmField
    val dateAdded: Long = System.currentTimeMillis()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HistoryEntity

        if (id != other.id) return false
        if (songId != other.songId) return false
        if (dateAdded != other.dateAdded) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + songId.hashCode()
        result = 31 * result + dateAdded.hashCode()
        return result
    }
}