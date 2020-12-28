package dev.olog.data.local.history

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "podcast_song_history",
    indices = [(Index("id"))]
)
data class PodcastHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val podcastId: Long,
    val dateAdded: Long = System.currentTimeMillis()
)