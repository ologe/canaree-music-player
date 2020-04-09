package dev.olog.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.data.model.db.PodcastPositionEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface PodcastPositionDao {

    @Query(
        """
        SELECT position
        FROM podcast_position
        WHERE id = :podcastId
    """
    )
    fun getPosition(podcastId: Long): Long?

    @Query("SELECT * FROM podcast_position")
    fun getAllPositions(): Flow<List<PodcastPositionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setPosition(entity: PodcastPositionEntity)

}