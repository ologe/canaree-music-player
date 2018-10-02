package dev.olog.msc.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import dev.olog.msc.data.entity.PodcastPositionEntity

@Dao
abstract class PodcastPositionDao {

    @Query("""
        SELECT position
        FROM podcast_position
        WHERE id = :podcastId
    """)
    abstract fun getPosition(podcastId: Long): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun setPosition(entity: PodcastPositionEntity)

}