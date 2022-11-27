package dev.olog.data.mapper

import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.entity.LastFmTrack
import dev.olog.data.api.lastfm.album.LastFmAlbumInfoDto
import dev.olog.data.api.lastfm.artist.LastFmArtistInfoDto
import dev.olog.data.api.lastfm.track.LastFmTrackInfoDto

fun LastFmTrackInfoDto.toDomain(id: Long): LastFmTrack? {
    val track = this.track ?: return null
    val artist = track.artist ?: return null
    val album = track.album ?: return null
    val image = album.image.reversed().find { it.text?.isNotBlank() == true }?.text

    return LastFmTrack(
        id = id,
        title = track.name.orEmpty(),
        artist = track.artist.name.orEmpty(),
        album = track.album.title.orEmpty(),
        image = image.orEmpty(),
        mbid = track.mbid.orEmpty(),
        artistMbid = artist.mbid.orEmpty(),
        albumMbid = album.mbid.orEmpty()
    )
}

fun LastFmAlbumInfoDto.toDomain(id: Long): LastFmAlbum? {
    val album = this.album ?: return null
    return LastFmAlbum(
        id = id,
        title = album.name.orEmpty(),
        artist = album.artist.orEmpty(),
        image = album.image.reversed().find { it.text?.isNotBlank() == true }?.text.orEmpty(),
        mbid = album.mbid.orEmpty(),
        wiki = album.wiki?.content.orEmpty(),
    )
}

fun LastFmArtistInfoDto.toDomain(id: Long): LastFmArtist? {
    val artist = this.artist ?: return null
    return LastFmArtist(
        id = id,
        image = "",
        mbid = artist.mbid.orEmpty(),
        wiki = artist.bio?.content.orEmpty()
    )
}