package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playing_queue",
    indices = [(Index("progressive"))]
)
class PlayingQueueEntity(
    @PrimaryKey(autoGenerate = true)
    @JvmField
    val progressive: Int = 0,
    @JvmField
    val category: String,
    @JvmField
    val categoryValue: String,
    @JvmField
    val songId: Long,
    @JvmField
    val idInPlaylist: Int
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayingQueueEntity

        if (progressive != other.progressive) return false
        if (category != other.category) return false
        if (categoryValue != other.categoryValue) return false
        if (songId != other.songId) return false
        if (idInPlaylist != other.idInPlaylist) return false

        return true
    }

    override fun hashCode(): Int {
        var result = progressive
        result = 31 * result + category.hashCode()
        result = 31 * result + categoryValue.hashCode()
        result = 31 * result + songId.hashCode()
        result = 31 * result + idInPlaylist
        return result
    }
}
