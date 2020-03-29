package dev.olog.core.gateway.spotify

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Album

interface SpotifyGateway {

    suspend fun getArtistAlbums(artistMediaId: MediaId.Category): List<Album>

}