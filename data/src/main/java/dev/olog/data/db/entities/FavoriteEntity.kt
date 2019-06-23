package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_songs",
        indices = [(Index("songId"))]
)
data class FavoriteEntity(
        @PrimaryKey var songId: Long
)

@Entity(tableName = "favorite_podcast_songs",
        indices = [(Index("podcastId"))]
)
data class FavoritePodcastEntity(
        @PrimaryKey var podcastId: Long
)
