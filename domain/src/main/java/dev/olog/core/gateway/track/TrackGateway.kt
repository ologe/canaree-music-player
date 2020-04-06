package dev.olog.core.gateway.track

import dev.olog.core.entity.PureUri
import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface TrackGateway {

    fun getAllTracks(): List<Song>
    fun getAllPodcasts(): List<Song>
    fun observeAllTracks(): Flow<List<Song>>
    fun observeAllPodcasts(): Flow<List<Song>>

    fun getByParam(param: Long): Song?
    fun observeByParam(param: Long): Flow<Song?>

    suspend fun deleteSingle(id: Long)
    suspend fun deleteGroup(ids: List<Long>)

    fun getByUri(uri: PureUri): Song?

    fun getByAlbumId(albumId: Long): Song?

}