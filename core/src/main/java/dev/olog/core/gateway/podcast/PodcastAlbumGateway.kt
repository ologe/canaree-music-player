package dev.olog.core.gateway.podcast

import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Id
import kotlinx.coroutines.flow.Flow

interface PodcastAlbumGateway {

    fun getAll(): List<Album>
    fun observeAll(): Flow<List<Album>>

    fun getById(id: Long): Album?
    fun observeById(id: Long): Flow<Album?>

    fun getTrackListByParam(id: Long): List<Song>
    fun observeTrackListByParam(id: Long): Flow<List<Song>>

    fun observeRecentlyPlayed(): Flow<List<Album>>
    suspend fun addRecentlyPlayed(id: Long)

    fun observeRecentlyAdded(): Flow<List<Album>>

    fun observeSiblings(id: Long): Flow<List<Album>>

    fun observeArtistsAlbums(artistId: Id): Flow<List<Album>>
}