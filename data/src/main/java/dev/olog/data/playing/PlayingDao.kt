package dev.olog.data.playing

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.olog.data.mediastore.song.MediaStoreSongsView
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PlayingDao {

    @Query("""
        SELECT songs_view.* 
        FROM songs_view JOIN playing ON songs_view.id = playing.id  
        WHERE songs_view.id = playing.id
        UNION 
        SELECT podcasts_view.* 
        FROM podcasts_view JOIN playing ON podcasts_view.id = playing.id  
        WHERE podcasts_view.id = playing.id
    """)
    abstract fun observe(): Flow<MediaStoreSongsView?>

    @Query("DELETE FROM playing")
    abstract suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: PlayingEntity)

    @Transaction
    open suspend fun updatePlaying(id: String) {
        clear()
        insert(PlayingEntity(id))
    }

}