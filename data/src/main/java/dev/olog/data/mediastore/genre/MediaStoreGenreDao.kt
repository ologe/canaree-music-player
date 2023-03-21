package dev.olog.data.mediastore.genre

import android.provider.MediaStore.*
import android.provider.MediaStore.Audio.*
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import dev.olog.data.mediastore.artist.MediaStoreArtistEntity
import dev.olog.data.mediastore.audio.MediaStoreAudioEntity
import dev.olog.data.queries.QueryUtils
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaStoreGenreDao {

    @Query("SELECT * FROM mediastore_genres ORDER BY genre")
    fun getAll(): List<MediaStoreGenreEntity>
    @Query("SELECT * FROM mediastore_genres ORDER BY genre")
    fun observeAll(): Flow<List<MediaStoreGenreEntity>>
    @Query("SELECT * FROM mediastore_genres WHERE genre_id = :id")
    fun getById(id: Long): MediaStoreGenreEntity?
    @Query("SELECT * FROM mediastore_genres WHERE genre_id = :id")
    fun observeById(id: Long): Flow<MediaStoreGenreEntity?>

    @RawQuery
    fun getTracks(query: SupportSQLiteQuery): List<MediaStoreAudioEntity>
    @RawQuery(observedEntities = [MediaStoreAudioEntity::class])
    fun observeTracks(query: SupportSQLiteQuery): Flow<List<MediaStoreAudioEntity>>

    @Query("""
        SELECT artist_id, artist, album_artist, is_podcast, count(*) as size, MAX(date_added) AS ${AudioColumns.DATE_ADDED}
        FROM mediastore_audio
        WHERE mediastore_audio.genre_id = :id AND artist <> '$UNKNOWN_STRING'
        GROUP BY artist_id
        ORDER BY artist
    """)
    fun observeRelatedArtists(id: Long): Flow<List<MediaStoreArtistEntity>>

    @Query("""
        SELECT * FROM mediastore_audio
        WHERE genre_id = :id AND ${QueryUtils.RECENTLY_ADDED}
        ORDER BY ${AudioColumns.DATE_ADDED} DESC, ${AudioColumns.TITLE} ASC
    """)
    fun observeRecentlyAddedSongs(id: Long): Flow<List<MediaStoreAudioEntity>>

}