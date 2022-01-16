package dev.olog.core.search

import dev.olog.core.MediaUri
import kotlinx.coroutines.flow.Flow

interface RecentSearchesGateway {

    fun observeAll() : Flow<List<SearchResult>>

    suspend fun insert(uri: MediaUri)
    suspend fun delete(uri: MediaUri)

    suspend fun deleteAll()

}