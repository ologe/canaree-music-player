package dev.olog.data.model.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "last_fm_track_v2",
    indices = [(Index("id"))]
)
data class LastFmTrackEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val image: String,
    val added: String,
    // new from v17
    val mbid: String,
    val artistMbid: String,
    val albumMbid: String
)