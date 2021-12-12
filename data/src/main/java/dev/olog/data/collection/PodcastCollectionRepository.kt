package dev.olog.data.collection

import dev.olog.core.DateTimeFactory
import dev.olog.core.entity.sort.CollectionDetailSort
import dev.olog.core.entity.sort.CollectionSort
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.podcast.PodcastCollectionGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.extension.mapToFlowList
import dev.olog.data.extension.mapToFlowOne
import dev.olog.data.extension.mapToFlowOneOrNull
import dev.olog.data.playable.Podcast_episodes_view
import dev.olog.data.playable.toDomain
import dev.olog.data.sort.SortDao
import dev.olog.shared.filterListItem
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class PodcastCollectionRepository @Inject constructor(
    private val schedulers: Schedulers,
    private val queries: PodcastCollectionQueries,
    private val sortDao: SortDao,
    private val dateTimeFactory: DateTimeFactory,
) : PodcastCollectionGateway {

    override fun getAll(): List<Album> {
        return queries.selectAllSorted()
            .executeAsList()
            .map(Podcast_collections_view::toDomain)
    }

    override fun observeAll(): Flow<List<Album>> {
        return queries.selectAllSorted()
            .mapToFlowList(schedulers.io)
            .mapListItem(Podcast_collections_view::toDomain)
    }

    override fun getByParam(param: Id): Album? {
        return queries.selectById(param)
            .executeAsOneOrNull()
            ?.toDomain()
    }

    override fun observeByParam(param: Id): Flow<Album?> {
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

    override fun observeRecentlyPlayed(): Flow<List<Album>> {
        return queries.selectRecentlyPlayed()
            .mapToFlowList(schedulers.io)
            .mapListItem(Podcast_collections_view::toDomain)
    }

    override suspend fun addRecentlyPlayed(id: Id) = withContext(schedulers.io) {
        queries.insertRecentlyPlayed(id, dateTimeFactory.currentTimeMillis())
    }

    override fun observeRecentlyAdded(): Flow<List<Album>> {
        return queries.selectRecentlyAdded()
            .mapToFlowList(schedulers.io)
            .mapListItem(Podcast_collections_view::toDomain)
    }

    override fun observeSiblings(param: Id): Flow<List<Album>> {
        return observeByParam(param)
            .filterNotNull()
            .flatMapLatest { observeArtistsAlbums(it.artistId) }
            .filterListItem { it.id != param }
    }

    override fun observeArtistsAlbums(artistId: Id): Flow<List<Album>> {
        return queries.selectArtistAlbums(artistId)
            .mapToFlowList(schedulers.io)
            .mapListItem(Podcast_collections_view::toDomain)
    }

    override fun getSort(): Sort<CollectionSort> {
        return sortDao.getPodcastCollectionsSortQuery().executeAsOne()
    }

    override fun setSort(sort: Sort<CollectionSort>) {
        sortDao.setPodcastCollectionsSort(sort)
    }

    override fun getDetailSort(): Sort<CollectionDetailSort> {
        return sortDao.getDetailPodcastCollectionsSortQuery().executeAsOne()
    }

    override fun observeDetailSort(): Flow<Sort<CollectionDetailSort>> {
        return sortDao.getDetailPodcastCollectionsSortQuery()
            .mapToFlowOne(schedulers.io)
    }

    override fun setDetailSort(sort: Sort<CollectionDetailSort>) {
        sortDao.setDetailPodcastCollectionsSort(sort)
    }

}