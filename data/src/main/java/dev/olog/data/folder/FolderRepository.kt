package dev.olog.data.folder

import dev.olog.core.MediaId
import dev.olog.core.entity.MostPlayedSong
import dev.olog.core.entity.sort.FolderDetailSort
import dev.olog.core.entity.sort.GenericSort
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.author.Artists_view
import dev.olog.data.author.toDomain
import dev.olog.data.extension.mapToFlowList
import dev.olog.data.extension.mapToFlowOne
import dev.olog.data.extension.mapToFlowOneOrNull
import dev.olog.data.playable.Songs_view
import dev.olog.data.playable.toDomain
import dev.olog.data.sort.SortDao
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class FolderRepository @Inject constructor(
    private val schedulers: Schedulers,
    private val queries: FoldersQueries,
    private val sortDao: SortDao,
) : FolderGateway {

    override fun getAll(): List<Folder> {
        return queries.selectAllSorted()
            .executeAsList()
            .map(Folders_view::toDomain)
    }

    override fun observeAll(): Flow<List<Folder>> {
        return queries.selectAllSorted()
            .mapToFlowList(schedulers.io)
            .mapListItem(Folders_view::toDomain)
    }

    override fun getByParam(param: String): Folder? {
        return queries.selectById(param)
            .executeAsOneOrNull()
            ?.toDomain()
    }

    override fun observeByParam(param: String): Flow<Folder?> {
        return queries.selectById(param)
            .mapToFlowOneOrNull(schedulers.io)
            .map { it?.toDomain() }
    }

    override fun getTrackListByParam(param: String): List<Song> {
        return queries.selectTracksByIdSorted(param)
            .executeAsList()
            .map(Songs_view::toDomain)
    }

    override fun observeTrackListByParam(param: String): Flow<List<Song>> {
        return queries.selectTracksByIdSorted(param)
            .mapToFlowList(schedulers.io)
            .mapListItem(Songs_view::toDomain)
    }

    override fun observeMostPlayed(mediaId: MediaId): Flow<List<MostPlayedSong>> {
        return queries.selectMostPlayed(mediaId.categoryValue)
            .mapToFlowList(schedulers.io)
            .mapListItem(SelectMostPlayed::toDomain)
    }

    override suspend fun insertMostPlayed(mediaId: MediaId) = withContext(schedulers.io) {
        val songId = mediaId.leaf!!
        val dir = mediaId.categoryValue
        queries.incrementMostPlayed(songId, dir)
    }

    override fun observeRecentlyAddedSongs(param: String): Flow<List<Song>> {
        return queries.selectRecentlyAddedSongs(param)
            .mapToFlowList(schedulers.io)
            .mapListItem(Songs_view::toDomain)
    }

    override fun observeRelatedArtists(params: String): Flow<List<Artist>> {
        return queries.selectRelatedArtists(params)
            .mapToFlowList(schedulers.io)
            .mapListItem(Artists_view::toDomain)
    }

    override fun observeSiblings(param: String): Flow<List<Folder>> {
        return queries.selectSiblings(param)
            .mapToFlowList(schedulers.io)
            .mapListItem(Folders_view::toDomain)
    }

    override fun getAllBlacklistedIncluded(): List<Folder> {
        return queries.selectAllBlacklistedIncluded { directory, songs, date_added ->
            Folders_view(directory = directory, songs = songs, date_added = date_added)
        }
            .executeAsList()
            .map(Folders_view::toDomain)
    }

    override fun getSort(): Sort<GenericSort> {
        return sortDao.getFoldersSortQuery().executeAsOne()
    }

    override fun setSort(sort: Sort<GenericSort>) {
        sortDao.setFoldersSort(sort)
    }

    override fun getDetailSort(): Sort<FolderDetailSort> {
        return sortDao.getDetailFoldersSortQuery().executeAsOne()
    }

    override fun observeDetailSort(): Flow<Sort<FolderDetailSort>> {
        return sortDao.getDetailFoldersSortQuery()
            .mapToFlowOne(schedulers.io)
    }

    override fun setDetailSort(sort: Sort<FolderDetailSort>) {
        sortDao.setDetailFoldersSort(sort)
    }
}