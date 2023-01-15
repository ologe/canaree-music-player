package dev.olog.core.gateway.track

import android.net.Uri
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Id
import kotlinx.coroutines.flow.Flow

interface SongGateway {

    suspend fun getAll(): List<Song>
    fun observeAll(): Flow<List<Song>>

    suspend fun getByParam(id: Long): Song?
    fun observeByParam(id: Long): Flow<Song?>

    suspend fun deleteSingle(id: Id)
    suspend fun deleteGroup(ids: List<Song>)

    suspend fun getByUri(uri: Uri): Song?

    suspend fun getByAlbumId(albumId: Id): Song?

}