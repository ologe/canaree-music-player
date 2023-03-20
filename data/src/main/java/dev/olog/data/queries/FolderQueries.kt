package dev.olog.data.queries

import android.provider.MediaStore.Audio.AudioColumns
import androidx.sqlite.db.SimpleSQLiteQuery
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.mediastore.MediaStoreArtistView
import dev.olog.data.mediastore.MediaStoreAudioView
import dev.olog.data.mediastore.MediaStoreAudioViewsDao
import dev.olog.data.mediastore.MediaStoreFolderView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

internal class FolderQueries @Inject constructor(
    private val dao: MediaStoreAudioViewsDao,
    private val sortPrefs: SortPreferences,
) {

    fun getAll(): List<MediaStoreFolderView> {
        return dao.getAllFolders()
    }

    suspend fun getAllFoldersBlacklistIncluded(): List<MediaStoreFolderView> {
        return dao.getAllFoldersBlacklistIncluded()
    }

    fun observeAll(): Flow<List<MediaStoreFolderView>> {
        return dao.observeAllFolders()
    }

    fun getById(id: Long): MediaStoreFolderView? {
        return dao.getByFolderId(id)
    }

    fun observeById(id: Long): Flow<MediaStoreFolderView?> {
        return dao.observeByFolderId(id)
    }

    fun getSongList(id: Long): List<MediaStoreAudioView> {
        val sort = sortPrefs.getDetailFolderSort()
        return dao.getFolderTracks(SimpleSQLiteQuery(getSongListQuery(sort), arrayOf(id)))
    }

    fun observeSongList(id: Long): Flow<List<MediaStoreAudioView>> {
        return sortPrefs.observeDetailFolderSort()
            .flatMapLatest { sort ->
                dao.observeFolderTracks(SimpleSQLiteQuery(getSongListQuery(sort), arrayOf(id)))
            }
    }

    private fun getSongListQuery(
        sort: SortEntity
    ): String {
        return """
            SELECT * FROM mediastore_audio
            WHERE ${AudioColumns.BUCKET_ID} = ?
            ORDER BY ${QueryUtils.songListSortOrder(sort, AudioColumns.TITLE)}
        """
    }

    fun observeRecentlyAdded(id: Long): Flow<List<MediaStoreAudioView>> {
        return dao.observeRecentlyAdded(id)
    }

    fun observeRelatedArtists(id: Long): Flow<List<MediaStoreArtistView>> {
        return dao.observeFolderRelatedArtists(id)
    }

    fun observeRelativePaths(): Flow<List<String>> {
        return dao.observeAllRelativePaths()
    }

    fun observeDirectories(relativePaths: List<String>): Flow<List<MediaStoreFolderView>> {
        return dao.observeDirectories(relativePaths)
    }

    fun observeDirectorySongs(relativePath: String): Flow<List<MediaStoreAudioView>> {
        return dao.observeDirectorySongs(relativePath)
    }

}