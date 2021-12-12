package dev.olog.data.recent.search

import dev.olog.core.DateTimeFactory
import dev.olog.core.MediaId
import dev.olog.core.entity.SearchResult
import dev.olog.core.gateway.RecentSearchesGateway
import dev.olog.core.schedulers.Schedulers
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
                    mediaId = it.media_id,
                    title = it.title
                )
            }
    }

    override suspend fun insert(mediaId: MediaId) = withContext(schedulers.io) {
        queries.insert(
            mediaId = mediaId,
            insertion_time = dateTimeFactory.currentTimeMillis()
        )
    }

    override suspend fun delete(mediaId: MediaId) = withContext(schedulers.io) {
        val itemId = when {
            mediaId.isLeaf -> mediaId.leaf!!.toString()
            else -> mediaId.categoryValue
        }
        queries.delete(itemId, mediaId.category.recentSearchType())
    }

    override suspend fun deleteAll() = withContext(schedulers.io) {
        queries.deleteAll()
    }
}