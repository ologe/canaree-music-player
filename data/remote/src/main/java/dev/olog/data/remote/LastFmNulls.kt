package dev.olog.data.remote

import dev.olog.domain.entity.LastFmAlbum
import dev.olog.domain.entity.LastFmArtist
import dev.olog.domain.entity.LastFmTrack

object LastFmNulls {

    internal fun createNullTrack(trackId: Long): LastFmTrack {
        return LastFmTrack(
            id = trackId,
            title = "",
            artist = "",
            album = "",
            image = "",
            mbid = "",
            artistMbid = "",
            albumMbid = ""
        )
    }

    internal fun createNullArtist(artistId: Long): LastFmArtist {
        return LastFmArtist(
            id = artistId,
            image = "",
            mbid = "",
            wiki = ""
        )
    }

    internal fun createNullAlbum(albumId: Long): LastFmAlbum {
        return LastFmAlbum(
            id = albumId,
            title = "",
            artist = "",
            image = "",
            mbid = "",
            wiki = ""
        )
    }

}
