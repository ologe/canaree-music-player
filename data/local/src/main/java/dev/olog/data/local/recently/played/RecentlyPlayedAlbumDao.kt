package dev.olog.data.local.recently.played

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RecentlyPlayedAlbumDao {

    @Query(
        """
        SELECT * FROM last_played_albums
        ORDER BY dateAdded DESC
        LIMIT 10
    """
    )
    abstract fun observeAll(): Flow<List<RecentlyPlayedAlbumEntity>>

    @Insert
    internal abstract suspend fun insertImpl(entity: RecentlyPlayedAlbumEntity)

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
        insertImpl(RecentlyPlayedAlbumEntity(id))
    }

}