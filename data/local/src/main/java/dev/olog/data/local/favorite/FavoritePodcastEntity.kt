package dev.olog.data.local.favorite

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_podcast_songs",
    indices = [(Index("podcastId"))]
)
data class FavoritePodcastEntity(
    @PrimaryKey
    val podcastId: Long
)