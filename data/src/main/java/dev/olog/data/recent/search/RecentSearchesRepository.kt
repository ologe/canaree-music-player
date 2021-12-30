package dev.olog.data.recent.search

import dev.olog.core.DateTimeFactory
import dev.olog.core.MediaUri
import dev.olog.core.schedulers.Schedulers
import dev.olog.core.search.RecentSearchesGateway
import dev.olog.core.search.SearchResult
import dev.olog.data.RecentSearchesQueries
import dev.olog.data.extension.mapToFlowList
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RecentSearchesRepository @Inject constructor(
    private val schedulers: Schedulers,
    private val queries: RecentSearchesQueries,
    private val dateTimeFactory: DateTimeFactory,
) : RecentSearchesGateway {

    override fun observeAll(): Flow<List<SearchResult>> {
        return queries.selectAll()
            .mapToFlowList(schedulers.io)
            .mapListItem {
                SearchResult(
                    uri = it.media_uri,
                    title = it.title
                )
            }
    }

    override suspend fun insert(uri: MediaUri) = withContext(schedulers.io) {
        queries.insert(
            uri = uri,
            insertion_time = dateTimeFactory.currentTimeMillis()
        )
    }

    override suspend fun delete(uri: MediaUri) = withContext(schedulers.io) {
        queries.delete(uri)
    }

    override suspend fun deleteAll() = withContext(schedulers.io) {
        queries.deleteAll()
    }
}