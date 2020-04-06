package dev.olog.domain.gateway.spotify

import dev.olog.domain.MediaId
import dev.olog.domain.entity.spotify.SpotifyAlbum
import dev.olog.domain.entity.spotify.SpotifyAlbumType
import dev.olog.domain.entity.spotify.SpotifyTrack

interface SpotifyGateway {

    suspend fun getArtistAlbums(artistMediaId: MediaId.Category, type: SpotifyAlbumType): List<SpotifyAlbum>

    suspend fun getArtistTopTracks(artistMediaId: MediaId.Category): List<SpotifyTrack>

    suspend fun getAlbumTracks(albumMediaId: MediaId.Category): List<SpotifyTrack>

    suspend fun getTrack(trackId: String): SpotifyTrack?

    fun getImage(spotifyUri: String): String?

}