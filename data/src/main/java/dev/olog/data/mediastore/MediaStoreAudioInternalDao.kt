package dev.olog.data.mediastore

import android.provider.MediaStore.*
import android.provider.MediaStore.Audio.*
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class MediaStoreAudioInternalDao {

    @Query("DELETE FROM mediastore_audio_internal")
    abstract suspend fun deleteAll()

    @Insert
    abstract suspend fun insertAll(items: List<MediaStoreAudioInternalEntity>)

    @Transaction
    open suspend fun replaceAll(items: List<MediaStoreAudioInternalEntity>) {
        deleteAll()
        insertAll(items)
    }

}