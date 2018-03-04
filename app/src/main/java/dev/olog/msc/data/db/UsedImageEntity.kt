package dev.olog.msc.data.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "used_image")
data class UsedImageEntity(
        @PrimaryKey val id: Long,
        val isAlbum: Boolean,
        val image: String
)