package dev.olog.core.gateway.track

import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface AlbumGateway {

    fun getAll(): List<Album>
    fun observeAll(): Flow<List<Album>>

    fun getById(id: Long): Album?
    fun observeById(id: Long): Flow<Album?>

    fun getTrackListById(id: Long): List<Song>
    fun observeTrackListById(id: Long): Flow<List<Song>>

    fun observeRecentlyPlayed(): Flow<List<Album>>
    suspend fun addRecentlyPlayed(id: Long)

    fun observeRecentlyAdded(): Flow<List<Album>>

    fun observeSiblings(id: Long): Flow<List<Album>>

    fun observeArtistsAlbums(artistId: Long): Flow<List<Album>>

}