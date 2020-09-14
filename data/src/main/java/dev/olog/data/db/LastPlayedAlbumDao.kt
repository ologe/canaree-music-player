package dev.olog.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.data.model.db.LastPlayedAlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface LastPlayedAlbumDao {

    // TODO use same limit as lastplayed artist dao, remove magic number
    @Query(
        """
        SELECT * FROM last_played_albums
        ORDER BY dateAdded DESC
        LIMIT 15
    """
    )
    fun getAll(): Flow<List<LastPlayedAlbumEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: LastPlayedAlbumEntity)

}