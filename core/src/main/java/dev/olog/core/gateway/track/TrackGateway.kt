package dev.olog.core.gateway.track

import dev.olog.core.entity.PureUri
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Id
import kotlinx.coroutines.flow.Flow

interface TrackGateway {

    fun getAllTracks(): List<Song>
    fun getAllPodcasts(): List<Song>
    fun observeAllTracks(): Flow<List<Song>>
    fun observeAllPodcasts(): Flow<List<Song>>

    fun getByParam(param: Id): Song?
    fun observeByParam(param: Id): Flow<Song?>

    suspend fun deleteSingle(id: Id)
    suspend fun deleteGroup(ids: List<Id>)

    fun getByUri(uri: PureUri): Song?

    fun getByAlbumId(albumId: Id): Song?

}