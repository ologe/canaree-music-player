package dev.olog.data.mediastore

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class MediaStoreAudioDao {

    @Query("SELECT * FROM mediastore_audio")
    abstract suspend fun getAll(): List<MediaStoreAudioEntity>

    @Query("DELETE FROM mediastore_audio")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM mediastore_audio WHERE id = :id")
    abstract suspend fun delete(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(items: List<MediaStoreAudioEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(vararg items: MediaStoreAudioEntity)

    @Transaction
    open suspend fun replaceAll(items: List<MediaStoreAudioEntity>) {
        deleteAll()
        insertAll(items)
    }

}