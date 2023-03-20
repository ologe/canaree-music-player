package dev.olog.data.mediastore.audio

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaStoreAudioDao {

    @RawQuery
    fun getAll(query: SupportSQLiteQuery): List<MediaStoreAudioEntity>
    @RawQuery(observedEntities = [MediaStoreAudioEntity::class])
    fun observeAll(query: SupportSQLiteQuery): Flow<List<MediaStoreAudioEntity>>

    @Query("SELECT * FROM mediastore_audio WHERE _id = :id")
    fun getById(id: Long): MediaStoreAudioEntity?
    @Query("SELECT * FROM mediastore_audio WHERE _id = :id")
    fun observeById(id: Long): Flow<MediaStoreAudioEntity?>
    @Query("SELECT * FROM mediastore_audio WHERE album_id = :albumId LIMIT 1")
    fun getByAlbumId(albumId: Long): MediaStoreAudioEntity?
    @Query("SELECT * FROM mediastore_audio WHERE _display_name = :displayName LIMIT 1")
    fun getByDisplayName(displayName: String): MediaStoreAudioEntity?

}