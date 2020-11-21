package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "last_fm_track_v2",
    indices = [(Index("id"))]
)
internal data class LastFmTrackEntity(
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
) {

    companion object

}

@Entity(
    tableName = "last_fm_album_v2",
    indices = [(Index("id"))]
)
internal data class LastFmAlbumEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val artist: String,
    val image: String,
    val added: String,
    // new from v17
    val mbid: String,
    val wiki: String
) {

    companion object

}

@Entity(
    tableName = "last_fm_artist_v2",
    indices = [(Index("id"))]
)
internal data class LastFmArtistEntity(
    @PrimaryKey
    val id: Long,
    val image: String,
    val added: String,
    // new from v17
    val mbid: String,
    val wiki: String
) {

    companion object

}

internal val LastFmTrackEntity.Companion.EMPTY: LastFmTrackEntity
    get() = LastFmTrackEntity(
        id = 0,
        title = "",
        artist = "",
        album = "",
        image = "",
        added = "",
        mbid = "",
        artistMbid = "",
        albumMbid = ""
    )

internal val LastFmAlbumEntity.Companion.EMPTY: LastFmAlbumEntity
    get() = LastFmAlbumEntity(
        id = 0,
        title = "",
        artist = "",
        image = "",
        added = "",
        mbid = "",
        wiki = ""
    )

internal val LastFmArtistEntity.Companion.EMPTY: LastFmArtistEntity
    get() = LastFmArtistEntity(
        id = 0,
        image = "",
        added = "",
        mbid = "",
        wiki = ""
    )
