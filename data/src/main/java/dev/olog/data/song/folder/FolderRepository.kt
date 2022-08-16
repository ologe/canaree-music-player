package dev.olog.data.song.folder

import dev.olog.core.entity.sort.AllFoldersSort
import dev.olog.core.entity.sort.FolderSongsSort
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.data.mediastore.song.artist.toDomain
import dev.olog.data.mediastore.song.folder.toDomain
import dev.olog.data.mediastore.song.toDomain
import dev.olog.data.sort.SortRepository
import dev.olog.shared.extension.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// todo test
internal class FolderRepository @Inject constructor(
    private val folderDao: FolderDao,
    private val sortRepository: SortRepository,
) : FolderGateway {

    override fun getAll(): List<Folder> {
        return folderDao.getAll()
            .map { it.toDomain() }
    }

    override fun observeAll(): Flow<List<Folder>> {
        return folderDao.observeAll()
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun getByParam(directory: String): Folder? {
        return folderDao.getByDirectory(directory)?.toDomain()
    }

    override fun observeByParam(directory: String): Flow<Folder?> {
        return folderDao.observeByDirectory(directory)
            .distinctUntilChanged()
            .map { it?.toDomain() }
    }

    override fun getByHashCode(hashCode: Int): Folder? = TODO()

    override fun getTrackListByParam(directory: String): List<Song> {
        return folderDao.getTracksByDirectory(directory)
            .map { it.toDomain() }
    }

    override fun observeTrackListByParam(directory: String): Flow<List<Song>> {
        return folderDao.observeTracksByDirectory(directory)
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun getAllBlacklistedIncluded(): List<Folder> {
        return folderDao.getAllBlacklistedIncluded()
            .map { it.toDomain() }
    }

    override fun observeMostPlayed(directory: String): Flow<List<Song>> {
        return folderDao.observeMostPlayed(directory)
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override suspend fun insertMostPlayed(directory: String, songId: Long) {
        folderDao.insertMostPlayed(
            directory = directory,
            songId = songId.toString(),
        )
    }

    override fun observeSiblings(directory: String): Flow<List<Folder>> {
        return folderDao.observeSiblings(directory)
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun observeRelatedArtists(directory: String): Flow<List<Artist>> {
        return folderDao.observeRelatedArtists(directory)
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun observeRecentlyAdded(directory: String): Flow<List<Song>> {
        return folderDao.observeRecentlyAddedSongs(directory)
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun setSort(sort: AllFoldersSort) {
        sortRepository.setAllFolderSort(sort)
    }

    override fun getSort(): AllFoldersSort {
        return sortRepository.getAllFoldersSort()
    }

    override fun setSongSort(sort: FolderSongsSort) {
        sortRepository.setFolderSongsSort(sort)
    }

    override fun getSongSort(): FolderSongsSort {
        return sortRepository.getFolderSongsSort()
    }
}