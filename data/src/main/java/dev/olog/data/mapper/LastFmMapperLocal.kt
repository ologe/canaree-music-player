package dev.olog.data.mapper

import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.entity.LastFmTrack
import dev.olog.data.db.entities.LastFmAlbumEntity
import dev.olog.data.db.entities.LastFmArtistEntity
import dev.olog.data.db.entities.LastFmTrackEntity
import java.text.SimpleDateFormat
import java.util.*

private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

private fun millisToFormattedDate(value: Long): String {
    return formatter.format(Date(value))

}

fun LastFmTrackEntity.toDomain(): LastFmTrack {
    return LastFmTrack(
        id = this.id,
        title = this.title,
        artist = this.artist,
        album = this.album,
        image = this.image,
        mbid = this.mbid,
        artistMbid = this.artistMbid,
        albumMbid = this.albumMbid
    )
}

fun LastFmAlbumEntity.toDomain(): LastFmAlbum {
    return LastFmAlbum(
        id = this.id,
        title = this.title,
        artist = this.artist,
        image = this.image,
        mbid = this.mbid,
        wiki = this.wiki
    )
}



fun LastFmTrack.toModel(): LastFmTrackEntity {
    return LastFmTrackEntity(
        id = this.id,
        title = this.title,
        artist = this.artist,
        album = this.album,
        image = this.image,
        added = millisToFormattedDate(System.currentTimeMillis()),
        mbid = this.mbid,
        artistMbid = this.artistMbid,
        albumMbid = this.albumMbid
    )
}



fun LastFmAlbum.toModel(): LastFmAlbumEntity {
    return LastFmAlbumEntity(
        id = this.id,
        title = this.title,
        artist = this.artist,
        image = this.image,
        added = millisToFormattedDate(System.currentTimeMillis()),
        mbid = this.mbid,
        wiki = this.wiki
    )
}

fun LastFmArtistEntity.toDomain(): LastFmArtist {
    return LastFmArtist(
        id = this.id,
        image = this.image,
        mbid = this.mbid,
        wiki = this.wiki
    )
}

fun LastFmArtist.toModel() : LastFmArtistEntity{
    return LastFmArtistEntity(
        id = this.id,
        image = this.image,
        added = millisToFormattedDate(System.currentTimeMillis()),
        mbid = this.mbid,
        wiki = this.wiki
    )
}

object LastFmNulls {

    fun createNullTrack(trackId: Long): LastFmTrackEntity {
        return LastFmTrackEntity(
            id = trackId,
            title = "",
            artist = "",
            album = "",
            image = "",
            added = millisToFormattedDate(System.currentTimeMillis()),
            mbid = "",
            artistMbid = "",
            albumMbid = ""
        )
    }

    fun createNullArtist(artistId: Long): LastFmArtistEntity {
        return LastFmArtistEntity(
            id = artistId,
            image = "",
            added = millisToFormattedDate(System.currentTimeMillis()),
            mbid = "",
            wiki = ""
        )
    }

    fun createNullAlbum(albumId: Long): LastFmAlbumEntity {
        return LastFmAlbumEntity(
            id = albumId,
            title = "",
            artist = "",
            image = "",
            added = millisToFormattedDate(System.currentTimeMillis()),
            mbid = "",
            wiki = ""
        )
    }

}
