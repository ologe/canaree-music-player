package dev.olog.data.mapper

import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.entity.LastFmTrack
import dev.olog.data.api.lastfm.album.info.AlbumInfo
import dev.olog.data.api.lastfm.album.search.AlbumSearch
import dev.olog.data.api.lastfm.artist.info.ArtistInfo
import dev.olog.data.api.lastfm.track.info.TrackInfo
import dev.olog.data.api.lastfm.track.search.TrackSearch
import me.xdrop.fuzzywuzzy.FuzzySearch

fun TrackInfo.toDomain(id: Long): LastFmTrack {
    val track = this.track
    val title = track.name
    val artist = track.artist.name
    val album = track.album.title
    val image = track.album.image.reversed().find { it.text.isNotBlank() }?.text

    return LastFmTrack(
        id,
        title ?: "",
        artist ?: "",
        album ?: "",
        image ?: ""
    )
}

fun TrackSearch.toDomain(id: Long): LastFmTrack? {
    val track = this.results.trackmatches.track[0]

    return LastFmTrack(
        id,
        track.name ?: "",
        track.artist ?: "",
        "",
        ""
    )
}

fun AlbumInfo.toDomain(id: Long): LastFmAlbum {
    val album = this.album
    return LastFmAlbum(
        id,
        album.name,
        album.artist,
        album.image.reversed().find { it.text.isNotBlank() }?.text ?: ""
    )
}

fun AlbumSearch.toDomain(id: Long, originalArtist: String): LastFmAlbum {
    val results = this.results.albummatches.album
    val bestArtist = FuzzySearch.extractOne(originalArtist, results.map { it.artist }).string
    val best = results.first { it.artist == bestArtist }

    return LastFmAlbum(
        id,
        best.name,
        best.artist,
        best.image.reversed().find { it.text.isNotBlank() }?.text ?: ""
    )
}

fun ArtistInfo.toDomain(id: Long): LastFmArtist {
    val artist = this.artist
    return LastFmArtist(
        id,
        artist.image.reversed().find { it.text.isNotBlank() }?.text ?: ""
    )
}