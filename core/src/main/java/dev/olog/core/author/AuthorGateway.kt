package dev.olog.core.author

import dev.olog.core.MediaStoreType
import dev.olog.core.MediaUri
import dev.olog.core.track.Song
import dev.olog.core.sort.AuthorDetailSort
import dev.olog.core.sort.AuthorSort
import dev.olog.core.sort.Sort
import kotlinx.coroutines.flow.Flow

interface AuthorGateway {

    fun getAll(type: MediaStoreType): List<Artist>
    fun observeAll(type: MediaStoreType): Flow<List<Artist>>

    fun getById(uri: MediaUri): Artist?
    fun observeById(uri: MediaUri): Flow<Artist?>

    fun getTracksById(uri: MediaUri): List<Song>
    fun observeTracksById(uri: MediaUri): Flow<List<Song>>

    fun observeRecentlyAdded(type: MediaStoreType): Flow<List<Artist>>

    fun observeRecentlyPlayed(type: MediaStoreType): Flow<List<Artist>>
    suspend fun addToRecentlyPlayed(uri: MediaUri)

    fun getSort(type: MediaStoreType): Sort<AuthorSort>
    fun setSort(type: MediaStoreType, sort: Sort<AuthorSort>)
    fun getDetailSort(type: MediaStoreType): Sort<AuthorDetailSort>
    fun observeDetailSort(type: MediaStoreType): Flow<Sort<AuthorDetailSort>>
    fun setDetailSort(type: MediaStoreType, sort: Sort<AuthorDetailSort>)

}