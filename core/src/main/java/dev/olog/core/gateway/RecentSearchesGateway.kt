package dev.olog.core.gateway

import dev.olog.core.MediaId
import dev.olog.core.entity.SearchResult
import kotlinx.coroutines.flow.Flow

interface RecentSearchesGateway {

    fun observeAll() : Flow<List<SearchResult>>

    suspend fun insert(mediaId: MediaId)
    suspend fun delete(mediaId: MediaId)

    suspend fun deleteAll()

}