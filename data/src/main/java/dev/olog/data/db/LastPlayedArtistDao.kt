package dev.olog.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.data.model.db.LastPlayedArtistEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface LastPlayedArtistDao {

    @Query(
        """
        SELECT * FROM last_played_artists
        ORDER BY dateAdded DESC
        LIMIT 15
    """
    )
    fun getAll(): Flow<List<LastPlayedArtistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: LastPlayedArtistEntity)

}
