package dev.olog.data.local.favorite

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