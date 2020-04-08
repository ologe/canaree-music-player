package dev.olog.lib.model.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "last_fm_artist_v2",
    indices = [(Index("id"))]
)
data class LastFmArtistEntity(
    @PrimaryKey
    val id: Long,
    val image: String,
    val added: String,
    // new from v17
    val mbid: String,
    val wiki: String
)