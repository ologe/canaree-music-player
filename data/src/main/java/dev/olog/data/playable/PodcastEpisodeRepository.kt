package dev.olog.data.playable

import dev.olog.core.entity.sort.PlayableSort
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.podcast.PodcastEpisodeGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.PodcastPositionQueries
import dev.olog.data.extension.mapToFlowList
import dev.olog.data.extension.mapToFlowOneOrNull
import dev.olog.data.index.IndexedPlayablesQueries
import dev.olog.data.index.Indexed_playables
import dev.olog.data.sort.SortDao
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.net.URI
import javax.inject.Inject

internal class PodcastEpisodeRepository @Inject constructor(
    private val schedulers: Schedulers,
    private val podcastPositionQueries: PodcastPositionQueries,
    private val queries: PodcastEpisodesQueries,
    private val indexedPlayablesQueries: IndexedPlayablesQueries,
    private val sortDao: SortDao,
    private val playableOperations: PlayableOperations,
) : PodcastEpisodeGateway {

    override fun getAll(): List<Song> {
        return queries.selectAllSorted()
            .executeAsList()
            .map(Indexed_playables::toDomain)
    }

    override fun observeAll(): Flow<List<Song>> {
        return queries.selectAllSorted()
            .mapToFlowList(schedulers.io)
            .mapListItem(Indexed_playables::toDomain)
    }

    override fun getByParam(param: Id): Song? {
        return queries.selectById(param)
            .executeAsOneOrNull()
            ?.toDomain()
    }

    override fun getByCollectionId(id: Id): Song? {
        return queries.selectByCollectionId(id)
            .executeAsOneOrNull()
            ?.toDomain()
    }

    override fun observeByParam(param: Id): Flow<Song?> {
        return queries.selectById(param)
            .mapToFlowOneOrNull(schedulers.io)
            .map { it?.toDomain() }
    }

    override suspend fun deleteSingle(id: Id): Unit = withContext(schedulers.io) {
        val idToDelete = playableOperations.delete(getByParam(id)) ?: return@withContext
        indexedPlayablesQueries.delete(idToDelete)
    }

    override suspend fun deleteGroup(playables: List<Song>) {
        val idsToDelete = playables.mapNotNull { playableOperations.delete(it) }
        queries.transaction {
            for (id in idsToDelete) {
                indexedPlayablesQueries.delete(id)
            }
        }
    }

    override fun getByUri(uri: URI): Song? {
        val id = playableOperations.getByUri(uri) ?: return null
        return getByParam(id)
    }

    // TODO
    override fun getCurrentPosition(podcastId: Long, duration: Long): Long {
        var position = podcastPositionQueries.selectById(podcastId)
            .executeAsOneOrNull()?.position ?: 0L
        position = position.coerceIn(0, duration)
        if (position > duration - 1000 * 5) {
            // if last 5 sec, restart
            return 0L
        }
        return position
    }

    override fun saveCurrentPosition(podcastId: Long, position: Long) {
        podcastPositionQueries.insert(podcastId, position)
    }

    override fun getSort(): Sort<PlayableSort> {
        return sortDao.getPodcastEpisodesSort().executeAsOne()
    }

    override fun setSort(sort: Sort<PlayableSort>) {
        sortDao.setPodcastEpisodesSort(sort)
    }

}