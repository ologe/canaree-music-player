package dev.olog.data.db.last.played

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class LastPlayedAlbumDao {

    @Query(
        """
        SELECT * FROM last_played_albums
        ORDER BY dateAdded DESC
        LIMIT 10
    """
    )
    abstract fun getAll(): Flow<List<LastPlayedAlbumEntity>>

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