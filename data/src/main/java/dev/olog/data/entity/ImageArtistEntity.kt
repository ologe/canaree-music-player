package dev.olog.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "artist_images",
        indices = arrayOf(Index("id")))
data class ImageArtistEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val key: Long,
        val image: String
)