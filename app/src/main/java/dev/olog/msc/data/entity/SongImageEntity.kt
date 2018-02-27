package dev.olog.msc.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "song_image",
        indices = [(Index(value = arrayOf("id")))]
)
data class SongImageEntity(
        @PrimaryKey val id: Long,
        val isAlbum: Boolean,
        val image: String
)