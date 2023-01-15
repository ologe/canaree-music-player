package dev.olog.data.mediastore.audio

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MediaStoreAudioDao {

    @Query("SELECT mediastore_view.* FROM mediastore_view")
    abstract fun observeAll(): Flow<List<MediaStoreAudioEntity>>

    @Query("SELECT mediastore_view.* FROM mediastore_view WHERE id = :id")
    abstract fun observeById(id: String): Flow<MediaStoreAudioEntity?>

    @Query("SELECT * FROM mediastore_view WHERE displayName = :displayName LIMIT 1")
    abstract suspend fun getByDisplayName(displayName: String): MediaStoreAudioEntity?

    @Query("SELECT * FROM mediastore_view WHERE albumId = :albumId")
    abstract suspend fun getByAlbumId(albumId: String): MediaStoreAudioEntity?

    @Query("DELETE FROM mediastore_audio")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM mediastore_audio WHERE id = :id")
    abstract suspend fun delete(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(items: List<MediaStoreAudioEntity>)

    @Transaction
    open suspend fun replaceAll(items: List<MediaStoreAudioEntity>) {
        deleteAll()
        insertAll(items)
    }

}