package dev.olog.msc.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "last_fm_track_info",
        indices = arrayOf(Index("id")))
data class LastFmTrackEntity(
        @PrimaryKey val id: Long,
        val title: String,
        val artist: String,
        val album: String
)