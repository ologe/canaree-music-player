package dev.olog.core.gateway.podcast

import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.PlaylistOperations
import kotlinx.coroutines.flow.Flow

interface PodcastPlaylistGateway : PlaylistOperations {

    fun getAll(): List<Playlist>
    fun observeAll(): Flow<List<Playlist>>

    fun getByParam(id: Long): Playlist?
    fun observeByParam(id: Long): Flow<Playlist?>

    fun getTrackListByParam(id: Long): List<Song>
    fun observeTrackListByParam(id: Long): Flow<List<Song>>

    fun observeSiblings(id: Long): Flow<List<Playlist>>

    fun observeRelatedArtists(id: Long): Flow<List<Artist>>

    fun getAllAutoPlaylists(): List<Playlist>

}