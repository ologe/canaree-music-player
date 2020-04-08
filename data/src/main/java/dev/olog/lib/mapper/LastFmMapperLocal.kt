package dev.olog.lib.mapper

import dev.olog.domain.entity.LastFmAlbum
import dev.olog.domain.entity.LastFmArtist
import dev.olog.domain.entity.LastFmTrack
import dev.olog.lib.model.db.LastFmAlbumEntity
import dev.olog.lib.model.db.LastFmArtistEntity
import dev.olog.lib.model.db.LastFmTrackEntity
import java.text.SimpleDateFormat
import java.util.*

private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

internal fun millisToFormattedDate(value: Long): String {
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

fun LastFmArtist.toModel() : LastFmArtistEntity {
    return LastFmArtistEntity(
        id = this.id,
        image = this.image,
        added = millisToFormattedDate(System.currentTimeMillis()),
        mbid = this.mbid,
        wiki = this.wiki
    )
}

