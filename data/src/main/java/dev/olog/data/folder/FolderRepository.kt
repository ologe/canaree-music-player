package dev.olog.data.folder

import dev.olog.core.MediaStoreFolderUri
import dev.olog.core.author.Artist
import dev.olog.core.entity.MostPlayedSong
import dev.olog.core.folder.Folder
import dev.olog.core.folder.FolderGateway
import dev.olog.core.MediaUri
import dev.olog.core.schedulers.Schedulers
import dev.olog.core.sort.FolderDetailSort
import dev.olog.core.sort.GenericSort
import dev.olog.core.sort.Sort
import dev.olog.core.track.Song
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

    override fun getById(uri: MediaUri): Folder? {
        return queries.selectById(uri.id)
            .executeAsOneOrNull()
            ?.toDomain()
    }

    override fun observeById(uri: MediaUri): Flow<Folder?> {
        return queries.selectById(uri.id)
            .mapToFlowOneOrNull(schedulers.io)
            .map { it?.toDomain() }
    }

    override fun getTracksById(uri: MediaUri): List<Song> {
        return queries.selectTracksByIdSorted(uri.id)
            .executeAsList()
            .map(Songs_view::toDomain)
    }

    override fun observeTracksById(uri: MediaUri): Flow<List<Song>> {
        return queries.selectTracksByIdSorted(uri.id)
            .mapToFlowList(schedulers.io)
            .mapListItem(Songs_view::toDomain)
    }

    override fun observeMostPlayed(uri: MediaUri): Flow<List<MostPlayedSong>> {
        return queries.selectMostPlayed(uri.id)
            .mapToFlowList(schedulers.io)
            .mapListItem(SelectMostPlayed::toDomain)
    }

    override suspend fun insertMostPlayed(
        uri: MediaUri,
        trackUri: MediaUri,
    ) = withContext(schedulers.io) {
        queries.incrementMostPlayed(
            id = trackUri.id,
            dir = uri.id,
        )
    }

    override fun observeRecentlyAddedTracksById(uri: MediaUri): Flow<List<Song>> {
        return queries.selectRecentlyAddedSongs(uri.id)
            .mapToFlowList(schedulers.io)
            .mapListItem(Songs_view::toDomain)
    }

    override fun observeRelatedArtistsById(uri: MediaUri): Flow<List<Artist>> {
        return queries.selectRelatedArtists(uri.id)
            .mapToFlowList(schedulers.io)
            .mapListItem(Artists_view::toDomain)
    }

    override fun observeSiblingsById(uri: MediaUri): Flow<List<Folder>> {
        return queries.selectSiblings(uri.id)
            .mapToFlowList(schedulers.io)
            .mapListItem(Folders_view::toDomain)
    }

    override fun getAllBlacklistedIncluded(): List<Folder> {
        return queries.selectAllBlacklistedIncluded()
            .executeAsList()
            .map {
                Folder(
                    uri = MediaStoreFolderUri(directory = it.directory),
                    directory = it.directory,
                    songs = it.songs.toInt()
                )
            }
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