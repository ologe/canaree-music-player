package dev.olog.data.db.podcast

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal abstract class PodcastPositionDao {

    @Query("""
        SELECT position
        FROM podcast_position
        WHERE id = :podcastId
    """)
    abstract fun getPosition(podcastId: Long): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun setPosition(entity: PodcastPositionEntity)

}