package dev.olog.data.mediastore

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class MediaStoreAudioDao {

    @Query("DELETE FROM mediastore_audio")
    abstract suspend fun deleteAll()

    @Insert
    abstract suspend fun insertAll(items: List<MediaStoreAudioEntity>)

    @Transaction
    open suspend fun replaceAll(items: List<MediaStoreAudioEntity>) {
        deleteAll()
        insertAll(items)
    }

}