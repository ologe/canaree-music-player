package dev.olog.core.gateway.track

import android.net.Uri
import dev.olog.core.entity.sort.AllSongsSort
import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface SongGateway {

    fun getAll(): List<Song>
    fun observeAll(): Flow<List<Song>>

    fun getByParam(id: Long): Song?
    fun observeByParam(id: Long): Flow<Song?>

    suspend fun deleteSingle(id: Long)
    suspend fun deleteGroup(ids: List<Long>)

    fun getByUri(uri: Uri): Song?

    fun getByAlbumId(albumId: Long): Song?

    fun setSort(sort: AllSongsSort)
    fun getSort(): AllSongsSort

}