package dev.olog.data.playable

import dev.olog.core.MediaUri
import dev.olog.core.schedulers.Schedulers
import dev.olog.core.sort.Sort
import dev.olog.core.sort.TrackSort
import dev.olog.core.track.Song
import dev.olog.data.extension.mapToFlowList
import dev.olog.data.extension.mapToFlowOneOrNull
import dev.olog.data.index.Indexed_playables
import dev.olog.data.sort.SortDao
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class MediaStoreSongRepository @Inject constructor(
    private val schedulers: Schedulers,
    private val queries: SongsQueries,
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

    fun getSort(): Sort<TrackSort> {
        return sortDao.getSongsSort().executeAsOne()
    }

    fun setSort(sort: Sort<TrackSort>) {
        sortDao.setSongsSort(sort)
    }
}