package dev.olog.core.collection

import dev.olog.core.MediaStoreType
import dev.olog.core.MediaUri
import dev.olog.core.sort.CollectionDetailSort
import dev.olog.core.sort.CollectionSort
import dev.olog.core.sort.Sort
import dev.olog.core.track.Song
import kotlinx.coroutines.flow.Flow

interface CollectionGateway {

    fun getAll(type: MediaStoreType): List<Album>
    fun observeAll(type: MediaStoreType): Flow<List<Album>>

    fun getById(uri: MediaUri): Album?
    fun observeById(uri: MediaUri): Flow<Album?>

    fun getTracksById(uri: MediaUri): List<Song>
    fun observeTracksById(uri: MediaUri): Flow<List<Song>>

    fun observeArtistsAlbums(authorUri: MediaUri): Flow<List<Album>>

    fun observeRecentlyAdded(type: MediaStoreType): Flow<List<Album>>

    fun observeRecentlyPlayed(type: MediaStoreType): Flow<List<Album>>
    suspend fun addToRecentlyPlayed(uri: MediaUri)

    fun observeSiblingsById(uri: MediaUri): Flow<List<Album>>

    fun getSort(type: MediaStoreType): Sort<CollectionSort>
    fun setSort(type: MediaStoreType, sort: Sort<CollectionSort>)
    fun getDetailSort(type: MediaStoreType): Sort<CollectionDetailSort>
    fun observeDetailSort(type: MediaStoreType): Flow<Sort<CollectionDetailSort>>
    fun setDetailSort(type: MediaStoreType, sort: Sort<CollectionDetailSort>)

}