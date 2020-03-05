package dev.olog.data.mapper

import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.entity.LastFmTrack
import dev.olog.data.model.lastfm.*
import me.xdrop.fuzzywuzzy.FuzzySearch
import timber.log.Timber

fun LastFmTrackInfo.toDomain(id: Long): LastFmTrack {
    val track = this.track
    val title = track.name
    val artist = track.artist.name
    val album = track.album.name
    val image = track.album.image.findBest()

    return LastFmTrack(
        id = id,
        title = title,
        artist = artist,
        album = album,
        image = image,
        mbid = track.mbid,
        artistMbid = track.artist.mbid ?: "",
        albumMbid = track.album.mbid
    )
}

fun LastFmTrackSearch.toDomain(id: Long): LastFmTrack {
    try {
        val track = this.results.trackmatches.track[0]

        return LastFmTrack(
            id = id,
            title = track.name,
            artist = track.artist,
            album = "",
            image = "",
            mbid = "",
            artistMbid = "",
            albumMbid = ""
        )
    } catch (ex: Throwable) {
        Timber.w(ex)
        return LastFmTrack(
            id = id,
            title = "",
            artist = "",
            album = "",
            image = "",
            mbid = "",
            artistMbid = "",
            albumMbid = ""
        )
    }
}

fun LastFmAlbumInfo.toDomain(id: Long): LastFmAlbum {
    val album = this.album
    return LastFmAlbum(
        id = id,
        title = album.name,
        artist = album.artist,
        image = album.image.findBest(),
        mbid = album.mbid,
        wiki = album.wiki?.content ?: ""
    )
}

fun LastFmAlbumSearch.toDomain(id: Long, originalArtist: String): LastFmAlbum {
    try {
        val results = this.results.albummatches.album
        val bestArtist = FuzzySearch.extractOne(originalArtist, results.map { it.artist }).string
        val best = results.first { it.artist == bestArtist }

        return LastFmAlbum(
            id = id,
            title = best.name,
            artist = best.artist,
            image = "",
            mbid = "",
            wiki = ""
        )
    } catch (ex: Throwable) {
        Timber.w(ex)
        return LastFmAlbum(
            id = id,
            title = "",
            artist = "",
            image = "",
            mbid = "",
            wiki = ""
        )
    }
}

fun LastFmArtistInfo.toDomain(id: Long): LastFmArtist? {
    val artist = this.artist
    return LastFmArtist(
        id = id,
        image = "",
        mbid = artist.mbid ?: "",
        wiki = artist.bio?.content ?: ""
    )
}

private fun List<LastFmImage>.findBest(): String {
    return this.reversed().first { it.text.isNotBlank() }.text
}