package dev.olog.lib.model.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_songs",
    indices = [(Index("songId"))]
)
data class FavoriteEntity(
    @PrimaryKey
    val songId: Long
)

@Entity(
    tableName = "favorite_podcast_songs",
    indices = [(Index("podcastId"))]
)
data class FavoritePodcastEntity(
    @PrimaryKey
    val podcastId: Long
)
