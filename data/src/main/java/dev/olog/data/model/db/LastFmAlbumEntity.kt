package dev.olog.data.model.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "last_fm_album_v2",
    indices = [(Index("id"))]
)
data class LastFmAlbumEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val artist: String,
    val image: String,
    val added: String,
    // new from v17
    val mbid: String,
    val wiki: String
)

