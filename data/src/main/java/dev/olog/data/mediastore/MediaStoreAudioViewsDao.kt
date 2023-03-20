package dev.olog.data.mediastore

import android.provider.MediaStore
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import dev.olog.data.queries.QueryUtils
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaStoreAudioViewsDao :
    MediaStoreAudioDao,
    MediaStoreFolderDao

interface MediaStoreAudioDao {

    @RawQuery
    fun getAll(query: SupportSQLiteQuery): List<MediaStoreAudioView>
    @RawQuery(observedEntities = [MediaStoreAudioView::class])
    fun observeAll(query: SupportSQLiteQuery): Flow<List<MediaStoreAudioView>>

    @Query("SELECT * FROM mediastore_audio WHERE _id = :id")
    fun getById(id: Long): MediaStoreAudioView?
    @Query("SELECT * FROM mediastore_audio WHERE _id = :id")
    fun observeById(id: Long): Flow<MediaStoreAudioView?>
    @Query("SELECT * FROM mediastore_audio WHERE album_id = :albumId LIMIT 1")
    fun getByAlbumId(albumId: Long): MediaStoreAudioView?
    @Query("SELECT * FROM mediastore_audio WHERE _display_name = :displayName LIMIT 1")
    fun getByDisplayName(displayName: String): MediaStoreAudioView?

}

interface MediaStoreFolderDao {

    @Query("SELECT * FROM mediastore_folders ORDER BY bucket_display_name")
    fun getAllFolders(): List<MediaStoreFolderView>
    @Query("SELECT * FROM mediastore_folders  ORDER BY bucket_display_name")
    fun observeAllFolders(): Flow<List<MediaStoreFolderView>>
    @Query("SELECT * FROM mediastore_folders WHERE bucket_id = :id")
    fun getByFolderId(id: Long): MediaStoreFolderView?
    @Query("SELECT * FROM mediastore_folders WHERE bucket_id = :id")
    fun observeByFolderId(id: Long): Flow<MediaStoreFolderView?>

    @Query("""
        SELECT bucket_id, bucket_display_name, relative_path, count(*) as size
        FROM mediastore_audio_internal
        GROUP BY bucket_id
        ORDER By bucket_display_name
    """)
    suspend fun getAllFoldersBlacklistIncluded(): List<MediaStoreFolderView>

    @RawQuery
    fun getFolderTracks(query: SupportSQLiteQuery): List<MediaStoreAudioView>
    @RawQuery(observedEntities = [MediaStoreAudioView::class])
    fun observeFolderTracks(query: SupportSQLiteQuery): Flow<List<MediaStoreAudioView>>

    @Query("""
        SELECT * FROM mediastore_audio
        WHERE ${MediaStore.Audio.AudioColumns.BUCKET_ID} = :id AND ${QueryUtils.RECENTLY_ADDED}
        ORDER BY ${MediaStore.Audio.AudioColumns.DATE_ADDED} DESC, ${MediaStore.Audio.AudioColumns.TITLE} ASC
    """)
    fun observeRecentlyAdded(id: Long): Flow<List<MediaStoreAudioView>>

    @Query("""
        SELECT artist_id, artist, album_artist, is_podcast, count(*) as size
        FROM mediastore_audio
        WHERE mediastore_audio.bucket_id = :id
        GROUP BY artist_id
        ORDER BY artist
    """)
    fun observeFolderRelatedArtists(id: Long): Flow<List<MediaStoreArtistView>>

    @Query("""
        SELECT relative_path
        FROM mediastore_folders
    """)
    fun observeAllRelativePaths(): Flow<List<String>>

    @Query("""
        SELECT *
        FROM mediastore_folders
        WHERE relative_path in (:relativePaths)
        ORDER BY relative_path COLLATE UNICODE
    """)
    fun observeDirectories(relativePaths: List<String>): Flow<List<MediaStoreFolderView>>

    @Query("""
        SELECT *
        FROM mediastore_audio
        WHERE relative_path = :relativePath
        ORDER BY title
    """)
    fun observeDirectorySongs(relativePath: String): Flow<List<MediaStoreAudioView>>

}