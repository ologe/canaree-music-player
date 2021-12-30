package dev.olog.data.playable

import dev.olog.core.MediaUri
import dev.olog.core.schedulers.Schedulers
import dev.olog.core.sort.Sort
import dev.olog.core.sort.TrackSort
import dev.olog.core.track.Song
import dev.olog.data.PodcastPositionQueries
import dev.olog.data.extension.mapToFlowList
import dev.olog.data.extension.mapToFlowOneOrNull
import dev.olog.data.index.Indexed_playables
import dev.olog.data.sort.SortDao
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class MediaStorePodcastEpisodeRepository @Inject constructor(
    private val schedulers: Schedulers,
    private val podcastPositionQueries: PodcastPositionQueries,
    private val queries: PodcastEpisodesQueries,
    private val sortDao: SortDao,
) {

    fun getAll(): List<Song> {
        return queries.selectAllSorted()
            .executeAsList()
            .map(Indexed_playables::toDomain)
    }

    fun observeAll(): Flow<List<Song>> {
        return queries.selectAllSorted()
            .mapToFlowList(schedulers.io)
            .mapListItem(Indexed_playables::toDomain)
    }

    fun getById(id: String): Song? {
        return queries.selectById(id)
            .executeAsOneOrNull()
            ?.toDomain()
    }

    fun getByCollectionId(collectionId: String): List<Song> {
        return queries.selectByCollectionId(collectionId)
            .executeAsList()
            .map(All_playables_view::toDomain)

    }

    fun observeById(id: String): Flow<Song?> {
        return queries.selectById(id)
            .mapToFlowOneOrNull(schedulers.io)
            .map { it?.toDomain() }
    }

    // TODO
    fun getCurrentPosition(id: String, duration: Long): Long {
        var position = podcastPositionQueries.selectById(id)
            .executeAsOneOrNull()?.position ?: 0L
        position = position.coerceIn(0, duration)
        if (position > duration - 1000 * 5) {
            // if last 5 sec, restart
            return 0L
        }
        return position
    }

    fun saveCurrentPosition(id: String, position: Long) {
        podcastPositionQueries.insert(id, position)
    }

    fun getSort(): Sort<TrackSort> {
        return sortDao.getPodcastEpisodesSort().executeAsOne()
    }

    fun setSort(sort: Sort<TrackSort>) {
        sortDao.setPodcastEpisodesSort(sort)
    }

}