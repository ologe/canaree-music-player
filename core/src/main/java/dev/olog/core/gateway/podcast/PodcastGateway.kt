package dev.olog.core.gateway.podcast

import android.net.Uri
import dev.olog.core.entity.sort.AllPodcastsSort
import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface PodcastGateway {

    fun getAll(): List<Song>
    fun observeAll(): Flow<List<Song>>

    fun getByParam(id: Long): Song?
    fun observeByParam(id: Long): Flow<Song?>

    suspend fun deleteSingle(id: Long)
    suspend fun deleteGroup(ids: List<Long>)

    fun getByUri(uri: Uri): Song?

    fun getCurrentPosition(podcastId: Long, duration: Long): Long
    fun saveCurrentPosition(podcastId: Long, position: Long)

    fun getByAlbumId(albumId: Long): Song?

    fun setSort(sort: AllPodcastsSort)
    fun getSort(): AllPodcastsSort

}