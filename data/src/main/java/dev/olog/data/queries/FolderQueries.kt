package dev.olog.data.queries

import dev.olog.data.mediastore.columns.AudioColumns
import androidx.sqlite.db.SimpleSQLiteQuery
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.mediastore.artist.MediaStoreArtistEntity
import dev.olog.data.mediastore.audio.MediaStoreAudioEntity
import dev.olog.data.mediastore.folder.MediaStoreFolderDao
import dev.olog.data.mediastore.folder.MediaStoreFolderEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

internal class FolderQueries @Inject constructor(
    private val dao: MediaStoreFolderDao,
    private val sortPrefs: SortPreferences,
) {

    fun getAll(): List<MediaStoreFolderEntity> {
        return dao.getAll()
    }

    suspend fun getAllFoldersBlacklistIncluded(): List<MediaStoreFolderEntity> {
        return dao.getAllBlacklistIncluded()
    }

    fun observeAll(): Flow<List<MediaStoreFolderEntity>> {
        return dao.observeAll()
    }

    fun getById(id: Long): MediaStoreFolderEntity? {
        return dao.getById(id)
    }

    fun observeById(id: Long): Flow<MediaStoreFolderEntity?> {
        return dao.observeById(id)
    }

    fun getSongList(id: Long): List<MediaStoreAudioEntity> {
        val sort = sortPrefs.getDetailFolderSort()
        return dao.getTracks(SimpleSQLiteQuery(getSongListQuery(sort), arrayOf(id)))
    }

    fun observeSongList(id: Long): Flow<List<MediaStoreAudioEntity>> {
        return sortPrefs.observeDetailFolderSort()
            .flatMapLatest { sort ->
                dao.observeTracks(SimpleSQLiteQuery(getSongListQuery(sort), arrayOf(id)))
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

    fun observeRecentlyAddedSongs(id: Long): Flow<List<MediaStoreAudioEntity>> {
        return dao.observeRecentlyAddedSongs(id)
    }

    fun observeRelatedArtists(id: Long): Flow<List<MediaStoreArtistEntity>> {
        return dao.observeRelatedArtists(id)
    }

    fun observeRelativePaths(): Flow<List<String>> {
        return dao.observeAllRelativePaths()
    }

    fun observeDirectories(relativePaths: List<String>): Flow<List<MediaStoreFolderEntity>> {
        return dao.observeDirectories(relativePaths)
    }

    fun observeDirectorySongs(relativePath: String): Flow<List<MediaStoreAudioEntity>> {
        return dao.observeDirectorySongs(relativePath)
    }

}