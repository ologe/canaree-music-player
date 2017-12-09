package dev.olog.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "folder_images",
        indices = arrayOf(Index("key")))
data class ImageFolderEntity(
        @PrimaryKey val key: String,
        val image: String
)