package dev.olog.data.mapper

import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.entity.LastFmTrack
import dev.olog.data.model.lastfm.*
import me.xdrop.fuzzywuzzy.FuzzySearch

fun LastFmTrackInfo.toDomain(id: Long): LastFmTrack {
    val track = this.track
    val title = track.name
    val artist = track.artist.name
    val album = track.album.name
    val image = track.album.image.findBest()

    return LastFmTrack(
        id,
        title,
        artist,
        album,
        image,
        track.mbid,
        track.artist.mbid,
        track.album.mbid
    )
}

fun LastFmTrackSearch.toDomain(id: Long): LastFmTrack {
    try {
        val track = this.results.trackmatches.track[0]

        return LastFmTrack(
            id,
            track.name,
            track.artist,
            "",
            "",
            "",
            "",
            ""
        )
    } catch (ex: Throwable) {
        ex.printStackTrace()
        return LastFmTrack(
            id,
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        )
    }
}

fun LastFmAlbumInfo.toDomain(id: Long): LastFmAlbum {
    val album = this.album
    return LastFmAlbum(
        id,
        album.name,
        album.artist,
        album.image.findBest(),
        album.mbid,
        album.wiki?.content ?: ""
    )
}

fun LastFmAlbumSearch.toDomain(id: Long, originalArtist: String): LastFmAlbum {
    try {
        val results = this.results.albummatches.album
        val bestArtist = FuzzySearch.extractOne(originalArtist, results.map { it.artist }).string
        val best = results.first { it.artist == bestArtist }

        return LastFmAlbum(
            id,
            best.name,
            best.artist,
            "",
            "",
            ""
        )
    } catch (ex: Throwable) {
        ex.printStackTrace()
        return LastFmAlbum(
            id,
            "",
            "",
            "",
            "",
            ""
        )
    }
}

fun LastFmArtistInfo.toDomain(id: Long): LastFmArtist? {
    val artist = this.artist
    return LastFmArtist(
        id,
        "",
        artist.mbid,
        artist.bio?.content ?: ""
    )
}

private fun List<LastFmImage>.findBest(): String {
    return this.reversed().first { it.text.isNotBlank() }.text
}