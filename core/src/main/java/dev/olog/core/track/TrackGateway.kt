package dev.olog.core.track

import dev.olog.core.MediaStoreType
import dev.olog.core.MediaUri
import dev.olog.core.sort.Sort
import dev.olog.core.sort.TrackSort
import kotlinx.coroutines.flow.Flow
import java.net.URI

interface TrackGateway {

    fun getAll(type: MediaStoreType): List<Song>
    fun observeAll(type: MediaStoreType): Flow<List<Song>>

    fun getById(uri: MediaUri): Song?
    fun observeById(uri: MediaUri): Flow<Song?>
    fun getByUri(uri: URI): Song?
    fun getByCollectionId(collectionUri: MediaUri): List<Song>

    suspend fun delete(uris: List<MediaUri>)

    fun getSort(type: MediaStoreType): Sort<TrackSort>
    fun setSort(type: MediaStoreType, sort: Sort<TrackSort>)
    fun getPodcastCurrentPosition(uri: MediaUri, duration: Long): Long
    fun savePodcastCurrentPosition(uri: MediaUri, position: Long)

}