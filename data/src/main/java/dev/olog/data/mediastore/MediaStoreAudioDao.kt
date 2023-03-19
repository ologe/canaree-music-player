package dev.olog.data.mediastore

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MediaStoreAudioDao {

    @Query("DELETE FROM mediastore_audio_internal")
    abstract suspend fun deleteAll()

    @Insert
    abstract suspend fun insertAll(items: List<MediaStoreAudioInternalEntity>)

    @Transaction
    open suspend fun replaceAll(items: List<MediaStoreAudioInternalEntity>) {
        deleteAll()
        insertAll(items)
    }

    @RawQuery
    abstract fun getAll(query: SupportSQLiteQuery): List<MediaStoreAudioView>
    @RawQuery(observedEntities = [MediaStoreAudioView::class])
    abstract fun observeAll(query: SupportSQLiteQuery): Flow<List<MediaStoreAudioView>>

    @Query("SELECT * FROM mediastore_audio WHERE _id = :id")
    abstract fun getById(id: Long): MediaStoreAudioView?
    @Query("SELECT * FROM mediastore_audio WHERE _id = :id")
    abstract fun observeById(id: Long): Flow<MediaStoreAudioView?>

    @Query("SELECT * FROM mediastore_audio WHERE album_id = :albumId LIMIT 1")
    abstract fun getByAlbumId(albumId: Long): MediaStoreAudioView?

}