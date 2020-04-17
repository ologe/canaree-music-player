package dev.olog.domain.gateway.spotify

import dev.olog.domain.MediaId
import dev.olog.domain.entity.spotify.SpotifyAlbum
import dev.olog.domain.entity.spotify.SpotifyAlbumType
import dev.olog.domain.entity.spotify.SpotifyTrack
import dev.olog.domain.entity.track.GeneratedPlaylist
import dev.olog.domain.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface SpotifyGateway {

    suspend fun getArtistAlbums(
        artistMediaId: MediaId.Category,
        type: SpotifyAlbumType
    ): List<SpotifyAlbum>

    suspend fun getArtistTopTracks(artistMediaId: MediaId.Category): List<SpotifyTrack>

    suspend fun getAlbumTracks(albumMediaId: MediaId.Category): List<SpotifyTrack>

    suspend fun getTrack(trackId: String): SpotifyTrack?

    fun observePlaylists(): Flow<List<GeneratedPlaylist>>
    fun observePlaylistByParam(id: Long): Flow<GeneratedPlaylist>

    fun getPlaylistsTracks(id: Long): List<Song>
    fun observePlaylistsTracks(id: Long): Flow<List<Song>>

    fun getImage(spotifyUri: String): String?

    fun fetchTracks()

    fun observeFetchStatus(): Flow<Int>

}