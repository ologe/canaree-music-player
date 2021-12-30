package dev.olog.data.author

import dev.olog.core.DateTimeFactory
import dev.olog.core.author.Artist
import dev.olog.core.schedulers.Schedulers
import dev.olog.core.sort.AuthorDetailSort
import dev.olog.core.sort.AuthorSort
import dev.olog.core.sort.Sort
import dev.olog.core.track.Song
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

internal class MediaStorePodcastAuthorRepository @Inject constructor(
    private val schedulers: Schedulers,
    private val queries: PodcastAuthorsQueries,
    private val sortDao: SortDao,
    private val dateTimeFactory: DateTimeFactory,
) {

    fun getAll(): List<Artist> {
        return queries.selectAllSorted()
            .executeAsList()
            .map(Podcast_authors_view::toDomain)
    }

    fun observeAll(): Flow<List<Artist>> {
        return queries.selectAllSorted()
            .mapToFlowList(schedulers.io)
            .mapListItem(Podcast_authors_view::toDomain)
    }

    fun getById(id: String): Artist? {
        return queries.selectById(id)
            .executeAsOneOrNull()
            ?.toDomain()
    }

    fun observeById(id: String): Flow<Artist?> {
        return queries.selectById(id)
            .mapToFlowOneOrNull(schedulers.io)
            .map { it?.toDomain() }
    }

    fun getTracksById(id: String): List<Song> {
        return queries.selectTracksByIdSorted(id)
            .executeAsList()
            .map(Podcast_episodes_view::toDomain)
    }

    fun observeTracksById(id: String): Flow<List<Song>> {
        return queries.selectTracksByIdSorted(id)
            .mapToFlowList(schedulers.io)
            .mapListItem(Podcast_episodes_view::toDomain)
    }

    fun observeRecentlyPlayed(): Flow<List<Artist>> {
        return queries.selectRecentlyPlayed()
            .mapToFlowList(schedulers.io)
            .mapListItem(Podcast_authors_view::toDomain)
    }

    suspend fun addToRecentlyPlayed(id: String) = withContext(schedulers.io) {
        queries.insertRecentlyPlayed(
            id = id,
            date_played = dateTimeFactory.currentTimeMillis()
        )
    }

    fun observeRecentlyAdded(): Flow<List<Artist>> {
        return queries.selectRecentlyAdded()
            .mapToFlowList(schedulers.io)
            .mapListItem(Podcast_authors_view::toDomain)
    }

    fun getSort(): Sort<AuthorSort> {
        return sortDao.getPodcastAuthorsSortQuery().executeAsOne()
    }

    fun setSort(sort: Sort<AuthorSort>) {
        sortDao.setPodcastAuthorsSort(sort)
    }

    fun getDetailSort(): Sort<AuthorDetailSort> {
        return sortDao.getDetailPodcastAuthorsSortQuery().executeAsOne()
    }

    fun observeDetailSort(): Flow<Sort<AuthorDetailSort>> {
        return sortDao.getDetailPodcastAuthorsSortQuery()
            .mapToFlowOne(schedulers.io)
    }

    fun setDetailSort(sort: Sort<AuthorDetailSort>) {
        sortDao.setDetailPodcastAuthorsSort(sort)
    }
}