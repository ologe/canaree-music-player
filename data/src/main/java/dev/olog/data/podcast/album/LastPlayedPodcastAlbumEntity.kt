package dev.olog.data.podcast.album

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "last_played_podcast_albums",
    indices = [(Index("id"))]
)
data class LastPlayedPodcastAlbumEntity(
    @PrimaryKey
    val id: Long,
    val dateAdded: Long = System.currentTimeMillis()
)