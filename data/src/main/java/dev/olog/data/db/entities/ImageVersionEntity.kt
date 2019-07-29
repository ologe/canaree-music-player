package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "image_version",
    indices = [(Index("mediaId"))]
)
data class ImageVersionEntity(
    @PrimaryKey val mediaId: String,
    val version: Int
)