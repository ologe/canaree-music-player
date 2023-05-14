package dev.olog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.data.db.entities.PlaylistMostPlayedEntity
import dev.olog.data.mediastore.audio.MediaStoreAudioEntity
import dev.olog.data.queries.QueryUtils
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistMostPlayedDao {

    @Query("""
        SELECT mediastore_audio.*
        FROM most_played_playlist JOIN mediastore_audio 
            ON most_played_playlist.songId = mediastore_audio._id
        WHERE most_played_playlist.playlistId = :playlistId
        GROUP BY most_played_playlist.songId
        HAVING count(*) >= ${QueryUtils.MOST_PLAYED_HAVE_AT_LEAST}
        ORDER BY count(*) DESC
        LIMIT ${QueryUtils.MOST_PLAYED_LIMIT}
    """)
    fun observe(playlistId: Long): Flow<List<MediaStoreAudioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(item: PlaylistMostPlayedEntity)

}
