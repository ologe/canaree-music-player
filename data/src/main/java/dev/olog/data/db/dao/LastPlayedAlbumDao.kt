package dev.olog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import dev.olog.data.db.entities.LastPlayedAlbumEntity
import io.reactivex.Flowable

@Dao
internal abstract class LastPlayedAlbumDao {

    @Query(
        """
        SELECT * FROM last_played_albums
        ORDER BY dateAdded DESC
        LIMIT 10
    """
    )
    abstract fun getAll(): Flowable<List<LastPlayedAlbumEntity>>

    @Insert
    internal abstract suspend fun insertImpl(entity: LastPlayedAlbumEntity)

    @Query(
        """
        DELETE FROM last_played_albums
        WHERE id = :albumId
    """
    )
    internal abstract suspend fun deleteImpl(albumId: Long)

    @Transaction
    open suspend fun insertOne(id: Long) {
        deleteImpl(id)
        insertImpl(LastPlayedAlbumEntity(id))
    }

}