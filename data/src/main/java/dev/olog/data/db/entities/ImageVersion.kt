package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "image_version",
    indices = [(Index("hash"))]
)
data class ImageVersion(
    @PrimaryKey val hash: Int,
    val version: Int
)