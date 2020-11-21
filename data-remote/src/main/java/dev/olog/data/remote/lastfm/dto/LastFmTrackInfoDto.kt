package dev.olog.data.remote.lastfm.dto

import dev.olog.core.entity.LastFmTrack

internal data class LastFmTrackInfoDto(
    val track: LastFmTrackInfoResultDto?
) {

    companion object {

        val EMPTY: LastFmTrackInfoDto
            get() = LastFmTrackInfoDto(
                track = null
            )

    }

}

internal data class LastFmTrackInfoResultDto(
    val artist: LastFmArtistDto?,
    val album: LastFmAlbumInfoResultDto?,
    val mbid: String?,
    val name: String?,
    val wiki: LastFmWikiDto?
) {

    companion object {

        val EMPTY: LastFmTrackInfoResultDto
            get() = LastFmTrackInfoResultDto(
                artist = null,
                album = null,
                mbid = null,
                name = null,
                wiki = null
            )

    }

}

internal data class LastFmArtistDto(
    val image: List<LastFmImageDto>?,
    val mbid: String?,
    val name: String?,
) {

    companion object {

        val EMPTY: LastFmArtistDto
            get() = LastFmArtistDto(
                image = null,
                mbid = null,
                name = null
            )

    }

}

internal fun LastFmTrackInfoDto.toDomain(id: Long): LastFmTrack? {
    val track = this.track ?: return null

    val title = track.name.orEmpty()
    val artist = track.artist?.name.orEmpty()
    val album = track.album?.name.orEmpty()
    val image = track.album?.image?.findLast { it.text.orEmpty().isNotBlank() }?.text ?: return null

    return LastFmTrack(
        id = id,
        title = title,
        artist = artist,
        album = album,
        image = image,
        mbid = track.mbid.orEmpty(),
        artistMbid = track.artist?.mbid.orEmpty(),
        albumMbid = track.album.mbid.orEmpty()
    )
}