package dev.olog.data.db.history

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "podcast_song_history",
    indices = [(Index("id"))]
)
@Deprecated("migrate to single table")
data class PodcastHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val podcastId: Long,
    val dateAdded: Long = System.currentTimeMillis()
)