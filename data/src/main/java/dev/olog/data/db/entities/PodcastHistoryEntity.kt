package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "podcast_song_history",
    indices = [(Index("id"))]
)
class PodcastHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @JvmField
    val id: Int = 0,
    @JvmField
    val podcastId: Long,
    @JvmField
    val dateAdded: Long = System.currentTimeMillis()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PodcastHistoryEntity

        if (id != other.id) return false
        if (podcastId != other.podcastId) return false
        if (dateAdded != other.dateAdded) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + podcastId.hashCode()
        result = 31 * result + dateAdded.hashCode()
        return result
    }
}