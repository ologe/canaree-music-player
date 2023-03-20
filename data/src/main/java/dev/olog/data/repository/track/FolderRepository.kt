package dev.olog.data.repository.track

import dev.olog.core.MediaId
import dev.olog.core.entity.VirtualFileSystemTree
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.data.db.dao.FolderMostPlayedDao
import dev.olog.data.db.entities.FolderMostPlayedEntity
import dev.olog.data.mediastore.toArtist
import dev.olog.data.mediastore.toFolder
import dev.olog.data.mediastore.toSong
import dev.olog.data.queries.FolderQueries
import dev.olog.shared.filterListItem
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class FolderRepository @Inject constructor(
    private val queries: FolderQueries,
    private val mostPlayedDao: FolderMostPlayedDao,
) : FolderGateway {

    override fun getAll(): List<Folder> {
        return queries.getAll().map { it.toFolder() }
    }

    override fun observeAll(): Flow<List<Folder>> {
        return queries.observeAll()
            .mapListItem { it.toFolder() }
    }

    override fun getById(id: Long): Folder? {
        return queries.getById(id)?.toFolder()
    }

    override fun observeById(id: Long): Flow<Folder?> {
        return queries.observeById(id).map { it?.toFolder() }
    }

    override fun getTrackListById(id: Long): List<Song> {
        return queries.getSongList(id).map { it.toSong() }
    }

    override fun observeTrackListById(id: Long): Flow<List<Song>> {
        return queries.observeSongList(id)
            .mapListItem { it.toSong() }
    }

    override fun observeTrackListByPath(relativePath: String): Flow<List<Song>> {
        return queries.observeDirectorySongs(relativePath)
            .mapListItem { it.toSong() }
    }

    override suspend fun getAllBlacklistedIncluded(): List<Folder> {
        return queries.getAllFoldersBlacklistIncluded().map { it.toFolder() }
    }

    override fun observeMostPlayed(mediaId: MediaId): Flow<List<Song>> {
        return mostPlayedDao.observe(mediaId.categoryId)
            .mapListItem { it.toSong() }
    }

    override suspend fun insertMostPlayed(mediaId: MediaId) {
        val entity = FolderMostPlayedEntity(
            songId = mediaId.leaf!!,
            folderId = mediaId.categoryId,
        )
        mostPlayedDao.insertOne(entity)
    }

    override fun observeSiblings(id: Long): Flow<List<Folder>> {
        return observeAll().filterListItem { it.id != id }
    }

    override fun observeRelatedArtists(id: Long): Flow<List<Artist>> {
        return queries.observeRelatedArtists(id)
            .mapListItem { it.toArtist() }
    }

    override fun observeRecentlyAdded(id: Long): Flow<List<Song>> {
        return queries.observeRecentlyAdded(id)
            .mapListItem { it.toSong() }
    }

    override fun observeFileSystem(): Flow<VirtualFileSystemTree> {
        return queries.observeRelativePaths()
            .map {  relativePaths ->
                VirtualFileSystemTree().apply {
                    for (relativePath in relativePaths) {
                        addPathRecursively(relativePath)
                    }
                }
            }
    }

    override fun observeDirectories(relativePaths: List<String>): Flow<List<Folder>> {
        return queries.observeDirectories(relativePaths)
            .mapListItem { it.toFolder() }
    }
}