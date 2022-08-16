package dev.olog.data.podcast.artist

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "last_played_podcast_artists",
    indices = [(Index("id"))]
)
data class LastPlayedPodcastArtistEntity(
    @PrimaryKey
    val id: Long,
    val dateAdded: Long = System.currentTimeMillis()
)