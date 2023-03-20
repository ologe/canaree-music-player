package dev.olog.data.mediastore.artist

import android.provider.MediaStore.*
import android.provider.MediaStore.Audio.*
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import dev.olog.data.mediastore.audio.MediaStoreAudioEntity
import dev.olog.data.queries.QueryUtils
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaStoreArtistDao {

    @RawQuery
    fun getAll(query: SupportSQLiteQuery): List<MediaStoreArtistEntity>
    @RawQuery(observedEntities = [MediaStoreArtistEntity::class])
    fun observeAll(query: SupportSQLiteQuery): Flow<List<MediaStoreArtistEntity>>
    @Query("SELECT * FROM mediastore_artists WHERE artist_id = :id")
    fun getById(id: Long): MediaStoreArtistEntity?
    @Query("SELECT * FROM mediastore_artists WHERE artist_id = :id")
    fun observeById(id: Long): Flow<MediaStoreArtistEntity?>

    @RawQuery
    fun getTracks(query: SupportSQLiteQuery): List<MediaStoreAudioEntity>
    @RawQuery(observedEntities = [MediaStoreAudioEntity::class])
    fun observeTracks(query: SupportSQLiteQuery): Flow<List<MediaStoreAudioEntity>>

    @Query("""
        SELECT artist_id, artist, album_artist, is_podcast, count(*) as size
        FROM mediastore_audio
        WHERE is_podcast = :isPodcast AND ${QueryUtils.RECENTLY_ADDED}
        GROUP BY artist_id
        ORDER BY artist
    """)
    fun observeRecentlyAdded(isPodcast: Boolean): Flow<List<MediaStoreArtistEntity>>

}