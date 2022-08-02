package dev.olog.core.gateway.track

import dev.olog.core.entity.sort.AllFoldersSort
import dev.olog.core.entity.sort.FolderSongsSort
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface FolderGateway {

    fun getAll(): List<Folder>
    fun observeAll(): Flow<List<Folder>>

    fun getByParam(directory: String): Folder?
    fun observeByParam(directory: String): Flow<Folder?>

    fun getTrackListByParam(directory: String): List<Song>
    fun observeTrackListByParam(directory: String): Flow<List<Song>>

    fun observeMostPlayed(directory: String): Flow<List<Song>>
    suspend fun insertMostPlayed(directory: String, songId: Long)

    fun observeSiblings(directory: String): Flow<List<Folder>>

    fun observeRelatedArtists(directory: String): Flow<List<Artist>>

    fun observeRecentlyAdded(directory: String): Flow<List<Song>>

    fun getAllBlacklistedIncluded(): List<Folder>

    /**
     * Hashcode = path.tohashCode()
     */
    @Deprecated("find an alternative")
    fun getByHashCode(hashCode: Int): Folder?

    fun setSort(sort: AllFoldersSort)
    fun getSort(): AllFoldersSort

    fun setSongSort(sort: FolderSongsSort)
    fun getSongSort(): FolderSongsSort

}