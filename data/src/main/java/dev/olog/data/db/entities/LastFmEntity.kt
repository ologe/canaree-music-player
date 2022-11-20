package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "last_fm_track_v2",
    indices = [(Index("id"))]
)
@Deprecated(message = "delete")
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

@Entity(
    tableName = "last_fm_album_v2",
    indices = [(Index("id"))]
)
@Deprecated(message = "delete")
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

@Entity(
    tableName = "last_fm_artist_v2",
    indices = [(Index("id"))]
)
@Deprecated(message = "delete")
data class LastFmArtistEntity(
    @PrimaryKey
    val id: Long,
    val image: String,
    val added: String,
    // new from v17
    val mbid: String,
    val wiki: String
)