package dev.olog.data.author

import dev.olog.core.DateTimeFactory
import dev.olog.core.entity.sort.AuthorDetailSort
import dev.olog.core.entity.sort.AuthorSort
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.podcast.PodcastAuthorGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.extension.mapToFlowList
import dev.olog.data.extension.mapToFlowOne
import dev.olog.data.extension.mapToFlowOneOrNull
import dev.olog.data.playable.Podcast_episodes_view
import dev.olog.data.playable.toDomain
import dev.olog.data.sort.SortDao
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class PodcastAuthorRepository @Inject constructor(
    private val schedulers: Schedulers,
    private val queries: PodcastAuthorsQueries,
    private val sortDao: SortDao,
    private val dateTimeFactory: DateTimeFactory,
) : PodcastAuthorGateway {

    override fun getAll(): List<Artist> {
        return queries.selectAllSorted()
            .executeAsList()
            .map(Podcast_authors_view::toDomain)
    }

    override fun observeAll(): Flow<List<Artist>> {
        return queries.selectAllSorted()
            .mapToFlowList(schedulers.io)
            .mapListItem(Podcast_authors_view::toDomain)
    }

    override fun getByParam(param: Id): Artist? {
        return queries.selectById(param)
            .executeAsOneOrNull()
            ?.toDomain()
    }

    override fun observeByParam(param: Id): Flow<Artist?> {
        return queries.selectById(param)
            .mapToFlowOneOrNull(schedulers.io)
            .map { it?.toDomain() }
    }

    override fun getTrackListByParam(param: Id): List<Song> {
        return queries.selectTracksByIdSorted(param)
            .executeAsList()
            .map(Podcast_episodes_view::toDomain)
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        return queries.selectTracksByIdSorted(param)
            .mapToFlowList(schedulers.io)
            .mapListItem(Podcast_episodes_view::toDomain)
    }

    override fun observeRecentlyPlayed(): Flow<List<Artist>> {
        return queries.selectRecentlyPlayed()
            .mapToFlowList(schedulers.io)
            .mapListItem(Podcast_authors_view::toDomain)
    }

    override suspend fun addRecentlyPlayed(id: Id) = withContext(schedulers.io) {
        queries.insertRecentlyPlayed(id, dateTimeFactory.currentTimeMillis())
    }

    override fun observeRecentlyAdded(): Flow<List<Artist>> {
        return queries.selectRecentlyAdded()
            .mapToFlowList(schedulers.io)
            .mapListItem(Podcast_authors_view::toDomain)
    }

    override fun getSort(): Sort<AuthorSort> {
        return sortDao.getPodcastAuthorsSortQuery().executeAsOne()
    }

    override fun setSort(sort: Sort<AuthorSort>) {
        sortDao.setPodcastAuthorsSort(sort)
    }

    override fun getDetailSort(): Sort<AuthorDetailSort> {
        return sortDao.getDetailPodcastAuthorsSortQuery().executeAsOne()
    }

    override fun observeDetailSort(): Flow<Sort<AuthorDetailSort>> {
        return sortDao.getDetailPodcastAuthorsSortQuery()
            .mapToFlowOne(schedulers.io)
    }

    override fun setDetailSort(sort: Sort<AuthorDetailSort>) {
        sortDao.setDetailPodcastAuthorsSort(sort)
    }
}