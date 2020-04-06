package dev.olog.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.data.model.db.GenreMostPlayedEntity
import dev.olog.data.model.db.MostTimesPlayedSongEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
internal abstract class GenreMostPlayedDao {

    @Query(
        """
        SELECT songId, count(*) as timesPlayed
        FROM most_played_genre
        WHERE genreId = :genreId
        GROUP BY songId
        HAVING count(*) >= 5
        ORDER BY timesPlayed DESC
        LIMIT 10
    """
    )
    abstract fun query(genreId: Long): Flow<List<MostTimesPlayedSongEntity>>

    @Insert
    abstract suspend fun insert(vararg item: GenreMostPlayedEntity)

    fun observeAll(genreId: Long, trackGateway: TrackGateway): Flow<List<Song>> {
        return this.query(genreId)
            .map { mostPlayed ->
                val songList = trackGateway.getAllTracks()
                mostPlayed.sortedByDescending { it.timesPlayed }
                    .mapNotNull { item -> songList.find { it.id == item.songId } }
            }
    }

}
