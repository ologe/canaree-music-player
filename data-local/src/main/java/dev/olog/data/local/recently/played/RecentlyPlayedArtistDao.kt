package dev.olog.data.local.recently.played

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RecentlyPlayedArtistDao {

    @Query(
        """
        SELECT * FROM last_played_artists
        ORDER BY dateAdded DESC
        LIMIT 20
    """
    )
    abstract fun getAll(): Flow<List<RecentlyPlayedArtistEntity>>

    @Insert
    internal abstract suspend fun insertImpl(entity: RecentlyPlayedArtistEntity)

    @Query(
        """
        DELETE FROM last_played_artists
        WHERE id = :artistId
    """
    )
    internal abstract suspend fun deleteImpl(artistId: Long)

    @Transaction
    open suspend fun insertOne(id: Long) {
        deleteImpl(id)
        insertImpl(RecentlyPlayedArtistEntity(id))
    }

}
