package dev.olog.data.mapper

import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.entity.LastFmTrack
import dev.olog.data.api.lastfm.album.AlbumInfo
import dev.olog.data.api.lastfm.album.AlbumSearch
import dev.olog.data.api.lastfm.artist.ArtistInfo
import dev.olog.data.api.lastfm.track.TrackInfo
import dev.olog.data.api.lastfm.track.TrackSearch
import me.xdrop.fuzzywuzzy.FuzzySearch

fun TrackInfo.toDomain(id: Long): LastFmTrack? {
    val track = this.track ?: return null
    val image = track.album?.image?.reversed()?.firstOrNull { it.text?.isNotBlank() == true }?.text

    return LastFmTrack(
        id = id,
        title = track.name ?: return null,
        artist = track.artist?.name ?: return null,
        album = track.album?.title ?: return null,
        image = image ?: return null,
        mbid = track.mbid.orEmpty(),
        artistMbid = track.artist.mbid.orEmpty(),
        albumMbid = track.album.mbid.orEmpty()
    )
}

fun TrackSearch.toDomain(id: Long): LastFmTrack? {
    val track = this.results?.trackmatches?.track?.firstOrNull() ?: return null

    return LastFmTrack(
        id = id,
        title = track.name ?: return null,
        artist = track.artist ?: return null,
        album = "",
        image = "",
        mbid = "",
        artistMbid = "",
        albumMbid = ""
    )
}

fun AlbumInfo.toDomain(id: Long): LastFmAlbum? {
    val album = this.album ?: return null
    return LastFmAlbum(
        id = id,
        title = album.name ?: return null,
        artist = album.artist ?: return null,
        image = album.image?.reversed()?.firstOrNull { it.text?.isNotBlank() == true }?.text ?: return null,
        mbid = album.mbid.orEmpty(),
        wiki = album.wiki?.content.orEmpty(),
    )
}

fun AlbumSearch.toDomain(id: Long, originalArtist: String): LastFmAlbum? {
    val results = this.results?.albummatches?.album ?: return null
    val bestArtist = FuzzySearch.extractOne(originalArtist, results.map { it.artist }).string
    val best = results.firstOrNull { it.artist == bestArtist } ?: return null

    return LastFmAlbum(
        id = id,
        title = best.name ?: return null,
        artist = best.artist ?: return null,
        image = "",
        mbid = "",
        wiki = ""
    )
}

fun ArtistInfo.toDomain(id: Long): LastFmArtist? {
    val artist = this.artist ?: return null
    return LastFmArtist(
        id = id,
        image = "",
        mbid = artist.mbid ?: "",
        wiki = artist.bio?.content ?: ""
    )
}