package dev.olog.core.gateway.track

import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface PlaylistGateway : PlaylistOperations {

    fun getAllAutoPlaylists(): List<Playlist>
    fun getAll(): List<Playlist>
    fun observeAll(): Flow<List<Playlist>>

    fun getByParam(id: String): Playlist?
    fun observeByParam(id: String): Flow<Playlist?>

    fun getTrackListByParam(id: String): List<Song>
    fun observeTrackListByParam(id: String): Flow<List<Song>>

    fun observeMostPlayed(id: String): Flow<List<Song>>
    suspend fun insertMostPlayed(playlistId: String, songId: String)

    fun observeSiblings(id: String): Flow<List<Playlist>>

    fun observeRelatedArtists(id: String): Flow<List<Artist>>

}