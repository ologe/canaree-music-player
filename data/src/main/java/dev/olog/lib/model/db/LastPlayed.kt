package dev.olog.lib.model.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "last_played_artists",
    indices = [(Index("id"))]
)
data class LastPlayedArtistEntity(
    @PrimaryKey
    val id: Long,
    val dateAdded: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "last_played_albums",
    indices = [(Index("id"))]
)
data class LastPlayedAlbumEntity(
    @PrimaryKey
    val id: Long,
    val dateAdded: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "last_played_podcast_artists",
    indices = [(Index("id"))]
)
data class LastPlayedPodcastArtistEntity(
    @PrimaryKey
    val id: Long,
    val dateAdded: Long = System.currentTimeMillis()
)