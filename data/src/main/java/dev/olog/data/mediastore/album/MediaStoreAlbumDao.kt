package dev.olog.data.mediastore.album

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import dev.olog.data.mediastore.audio.MediaStoreAudioEntity
import dev.olog.data.queries.QueryUtils
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaStoreAlbumDao {

    @RawQuery
    fun getAll(query: SupportSQLiteQuery): List<MediaStoreAlbumEntity>
    @RawQuery(observedEntities = [MediaStoreAlbumEntity::class])
    fun observeAll(query: SupportSQLiteQuery): Flow<List<MediaStoreAlbumEntity>>
    @Query("SELECT * FROM mediastore_albums WHERE album_id = :id")
    fun getById(id: Long): MediaStoreAlbumEntity?
    @Query("SELECT * FROM mediastore_albums WHERE album_id = :id")
    fun observeById(id: Long): Flow<MediaStoreAlbumEntity?>

    @RawQuery
    fun getTracks(query: SupportSQLiteQuery): List<MediaStoreAudioEntity>
    @RawQuery(observedEntities = [MediaStoreAudioEntity::class])
    fun observeTracks(query: SupportSQLiteQuery): Flow<List<MediaStoreAudioEntity>>

    @Query("""
        SELECT *
        FROM mediastore_albums
        WHERE is_podcast = :isPodcast AND ${QueryUtils.RECENTLY_ADDED}
        ORDER BY date_added DESC
    """)
    fun observeRecentlyAdded(isPodcast: Int): Flow<List<MediaStoreAlbumEntity>>

    @Query("""
        SELECT *
        FROM mediastore_albums
        WHERE album_id != :id AND artist_id = (
            SELECT artist_id 
            FROM mediastore_albums
            WHERE album_id = :id
            LIMIT 1
        )
        ORDER BY album
    """)
    fun observeSiblings(id: Long): Flow<List<MediaStoreAlbumEntity>>

}