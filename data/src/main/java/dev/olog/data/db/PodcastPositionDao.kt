package dev.olog.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.data.model.db.PodcastPositionEntity

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setPosition(entity: PodcastPositionEntity)

}