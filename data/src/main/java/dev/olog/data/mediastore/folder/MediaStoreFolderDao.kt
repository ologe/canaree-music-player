package dev.olog.data.mediastore.folder

import android.provider.MediaStore
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import dev.olog.data.mediastore.artist.MediaStoreArtistEntity
import dev.olog.data.mediastore.audio.MediaStoreAudioEntity
import dev.olog.data.queries.QueryUtils
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaStoreFolderDao {

    @Query("SELECT * FROM mediastore_folders ORDER BY bucket_display_name")
    fun getAll(): List<MediaStoreFolderEntity>
    @Query("SELECT * FROM mediastore_folders  ORDER BY bucket_display_name")
    fun observeAll(): Flow<List<MediaStoreFolderEntity>>
    @Query("SELECT * FROM mediastore_folders WHERE bucket_id = :id")
    fun getById(id: Long): MediaStoreFolderEntity?
    @Query("SELECT * FROM mediastore_folders WHERE bucket_id = :id")
    fun observeById(id: Long): Flow<MediaStoreFolderEntity?>

    @Query("""
        SELECT bucket_id, bucket_display_name, relative_path, count(*) as size
        FROM mediastore_audio_internal
        GROUP BY bucket_id
        ORDER By bucket_display_name
    """)
    suspend fun getAllBlacklistIncluded(): List<MediaStoreFolderEntity>

    @RawQuery
    fun getTracks(query: SupportSQLiteQuery): List<MediaStoreAudioEntity>
    @RawQuery(observedEntities = [MediaStoreAudioEntity::class])
    fun observeTracks(query: SupportSQLiteQuery): Flow<List<MediaStoreAudioEntity>>

    @Query("""
        SELECT * FROM mediastore_audio
        WHERE ${MediaStore.Audio.AudioColumns.BUCKET_ID} = :id AND ${QueryUtils.RECENTLY_ADDED}
        ORDER BY ${MediaStore.Audio.AudioColumns.DATE_ADDED} DESC, ${MediaStore.Audio.AudioColumns.TITLE} ASC
    """)
    fun observeRecentlyAdded(id: Long): Flow<List<MediaStoreAudioEntity>>

    @Query("""
        SELECT artist_id, artist, album_artist, is_podcast, count(*) as size
        FROM mediastore_audio
        WHERE mediastore_audio.bucket_id = :id
        GROUP BY artist_id
        ORDER BY artist
    """)
    fun observeRelatedArtists(id: Long): Flow<List<MediaStoreArtistEntity>>

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
    fun observeDirectories(relativePaths: List<String>): Flow<List<MediaStoreFolderEntity>>

    @Query("""
        SELECT *
        FROM mediastore_audio
        WHERE relative_path = :relativePath
        ORDER BY title
    """)
    fun observeDirectorySongs(relativePath: String): Flow<List<MediaStoreAudioEntity>>

}