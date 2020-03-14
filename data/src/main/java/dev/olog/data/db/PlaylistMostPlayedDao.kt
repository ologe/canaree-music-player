package dev.olog.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.TrackGateway
import dev.olog.data.model.db.PlaylistMostPlayedEntity
import dev.olog.data.model.db.MostTimesPlayedSongEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
internal abstract class PlaylistMostPlayedDao {

    @Query(
        """
        SELECT songId, count(*) as timesPlayed
        FROM most_played_playlist
        WHERE playlistId = :playlistId
        GROUP BY songId
        HAVING count(*) >= 5
        ORDER BY timesPlayed DESC
        LIMIT 10
    """
    )
    abstract fun query(playlistId: Long): Flow<List<MostTimesPlayedSongEntity>>

    @Insert
    abstract suspend fun insert(vararg item: PlaylistMostPlayedEntity)

    fun observeAll(playlistId: Long, trackGateway: TrackGateway): Flow<List<Song>> {
        return this.query(playlistId)
            .map { mostPlayed ->
                val songList = trackGateway.getAllTracks()
                mostPlayed.sortedByDescending { it.timesPlayed }
                    .mapNotNull { item -> songList.find { it.id == item.songId } }
            }
    }

}
