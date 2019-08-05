package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "last_fm_track_v2",
    indices = [(Index("id"))]
)
class LastFmTrackEntity(
    @JvmField
    @PrimaryKey val id: Long,
    @JvmField
    val title: String,
    @JvmField
    val artist: String,
    @JvmField
    val album: String,
    @JvmField
    val image: String,
    @JvmField
    val added: String,
    // new from v17
    @JvmField
    val mbid: String,
    @JvmField
    val artistMbid: String,
    @JvmField
    val albumMbid: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LastFmTrackEntity

        if (id != other.id) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (album != other.album) return false
        if (image != other.image) return false
        if (added != other.added) return false
        if (mbid != other.mbid) return false
        if (artistMbid != other.artistMbid) return false
        if (albumMbid != other.albumMbid) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + added.hashCode()
        result = 31 * result + mbid.hashCode()
        result = 31 * result + artistMbid.hashCode()
        result = 31 * result + albumMbid.hashCode()
        return result
    }
}

@Entity(
    tableName = "last_fm_album_v2",
    indices = [(Index("id"))]
)
class LastFmAlbumEntity(
    @PrimaryKey
    @JvmField
    val id: Long,
    @JvmField
    val title: String,
    @JvmField
    val artist: String,
    @JvmField
    val image: String,
    @JvmField
    val added: String,
    // new from v17
    @JvmField
    val mbid: String,
    @JvmField
    val wiki: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LastFmAlbumEntity

        if (id != other.id) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (image != other.image) return false
        if (added != other.added) return false
        if (mbid != other.mbid) return false
        if (wiki != other.wiki) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + added.hashCode()
        result = 31 * result + mbid.hashCode()
        result = 31 * result + wiki.hashCode()
        return result
    }
}

@Entity(
    tableName = "last_fm_artist_v2",
    indices = [(Index("id"))]
)
class LastFmArtistEntity(
    @PrimaryKey
    @JvmField
    val id: Long,
    @JvmField
    val image: String,
    @JvmField
    val added: String,
    // new from v17
    @JvmField
    val mbid: String,
    @JvmField
    val wiki: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LastFmArtistEntity

        if (id != other.id) return false
        if (image != other.image) return false
        if (added != other.added) return false
        if (mbid != other.mbid) return false
        if (wiki != other.wiki) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + added.hashCode()
        result = 31 * result + mbid.hashCode()
        result = 31 * result + wiki.hashCode()
        return result
    }
}