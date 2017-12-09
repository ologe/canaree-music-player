package dev.olog.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "playlist_images",
        indices = arrayOf(Index("id")))
data class ImagePlaylistEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val key: Long,
        val image: String
)