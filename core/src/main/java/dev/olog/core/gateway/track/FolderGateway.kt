package dev.olog.core.gateway.track

import dev.olog.core.MediaId
import dev.olog.core.entity.VirtualFileSystemTree
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface FolderGateway {

    fun getAll(): List<Folder>
    fun observeAll(): Flow<List<Folder>>
    suspend fun getAllBlacklistedIncluded(): List<Folder>

    fun getById(id: Long): Folder?
    fun observeById(id: Long): Flow<Folder?>

    fun getTrackListById(id: Long): List<Song>
    fun observeTrackListById(id: Long): Flow<List<Song>>
    fun observeTrackListByPath(relativePath: String): Flow<List<Song>>

    fun observeMostPlayed(mediaId: MediaId): Flow<List<Song>>
    suspend fun insertMostPlayed(mediaId: MediaId)

    fun observeSiblings(id: Long): Flow<List<Folder>>

    fun observeRelatedArtists(id: Long): Flow<List<Artist>>

    fun observeRecentlyAdded(id: Long): Flow<List<Song>>

    fun observeFileSystem(): Flow<VirtualFileSystemTree>
    fun observeDirectories(relativePaths: List<String>): Flow<List<Folder>>

}