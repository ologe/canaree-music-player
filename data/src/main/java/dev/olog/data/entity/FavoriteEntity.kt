package dev.olog.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "favorite_songs",
        indices = [(Index(value = ["songId"]))]
)
data class FavoriteEntity(
        @PrimaryKey var songId: Long
)
