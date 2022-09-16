package dev.olog.data.favourites

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favourites",
    indices = [(Index("id"))]
)
data class FavoriteEntity(
    @PrimaryKey
    val id: String,
)