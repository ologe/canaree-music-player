package dev.olog.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.data.model.db.LastPlayedPodcastAlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface LastPlayedPodcastAlbumDao {

    @Query(
        """
        SELECT * FROM last_played_podcast_albums
        ORDER BY dateAdded DESC
        LIMIT 10
    """
    )
    fun getAll(): Flow<List<LastPlayedPodcastAlbumEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: LastPlayedPodcastAlbumEntity)

}