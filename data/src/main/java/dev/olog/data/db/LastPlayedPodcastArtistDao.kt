package dev.olog.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.data.model.db.LastPlayedPodcastArtistEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface LastPlayedPodcastArtistDao {

    @Query(
        """
        SELECT * FROM last_played_podcast_artists
        ORDER BY dateAdded DESC
        LIMIT 20
    """
    )
    fun getAll(): Flow<List<LastPlayedPodcastArtistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: LastPlayedPodcastArtistEntity)

}
