package dev.olog.data.collection

import dev.olog.core.DateTimeFactory
import dev.olog.core.collection.Album
import dev.olog.core.MediaUri
import dev.olog.core.track.Song
import dev.olog.core.schedulers.Schedulers
import dev.olog.core.sort.CollectionDetailSort
import dev.olog.core.sort.CollectionSort
import dev.olog.core.sort.Sort
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
import javax.inject.Inject

internal class MediaStorePodcastCollectionRepository @Inject constructor(
    private val schedulers: Schedulers,
    private val queries: PodcastCollectionQueries,
    private val sortDao: SortDao,
    private val dateTimeFactory: DateTimeFactory,
) {

    fun getAll(): List<Album> {
        return queries.selectAllSorted()
            .executeAsList()
            .map(Podcast_collections_view::toDomain)
    }

    fun observeAll(): Flow<List<Album>> {
        return queries.selectAllSorted()
            .mapToFlowList(schedulers.io)
            .mapListItem(Podcast_collections_view::toDomain)
    }

    fun getById(id: String): Album? {
        return queries.selectById(id)
            .executeAsOneOrNull()
            ?.toDomain()
    }

    fun observeById(id: String): Flow<Album?> {
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

    fun observeRecentlyPlayed(): Flow<List<Album>> {
        return queries.selectRecentlyPlayed()
            .mapToFlowList(schedulers.io)
            .mapListItem(Podcast_collections_view::toDomain)
    }

    fun addToRecentlyPlayed(id: String) {
        queries.insertRecentlyPlayed(
            id = id,
            date_played = dateTimeFactory.currentTimeMillis()
        )
    }

    fun observeRecentlyAdded(): Flow<List<Album>> {
        return queries.selectRecentlyAdded()
            .mapToFlowList(schedulers.io)
            .mapListItem(Podcast_collections_view::toDomain)
    }

    fun observeSiblingsById(id: String): Flow<List<Album>> {
        return observeById(id)
            .filterNotNull()
            .flatMapLatest { observeArtistsAlbums(it.artistUri.id) }
            .filterListItem { it.uri.id != id }
    }

    fun observeArtistsAlbums(artistId: String): Flow<List<Album>> {
        return queries.selectArtistAlbums(artistId)
            .mapToFlowList(schedulers.io)
            .mapListItem(Podcast_collections_view::toDomain)
    }

    fun getSort(): Sort<CollectionSort> {
        return sortDao.getPodcastCollectionsSortQuery().executeAsOne()
    }

    fun setSort(sort: Sort<CollectionSort>) {
        sortDao.setPodcastCollectionsSort(sort)
    }

    fun getDetailSort(): Sort<CollectionDetailSort> {
        return sortDao.getDetailPodcastCollectionsSortQuery().executeAsOne()
    }

    fun observeDetailSort(): Flow<Sort<CollectionDetailSort>> {
        return sortDao.getDetailPodcastCollectionsSortQuery()
            .mapToFlowOne(schedulers.io)
    }

    fun setDetailSort(sort: Sort<CollectionDetailSort>) {
        sortDao.setDetailPodcastCollectionsSort(sort)
    }

}