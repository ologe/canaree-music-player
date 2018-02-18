package dev.olog.msc.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "last_fm_track_image",
        indices = arrayOf(Index("id")))
data class LastFmTrackImageEntity(
        @PrimaryKey val id: Long,
        val image: String,
        val use: Boolean
)