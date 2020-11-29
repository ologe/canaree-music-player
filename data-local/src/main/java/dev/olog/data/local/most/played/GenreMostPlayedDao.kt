package dev.olog.data.local.most.played

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.SongGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
abstract class GenreMostPlayedDao {

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
    abstract fun observeAllImpl(genreId: Long): Flow<List<SongMostTimesPlayedEntity>>

    @Insert
    abstract suspend fun insertOne(item: GenreMostPlayedEntity)

    fun observeAll(
        playlistId: Long,
        songGateway: SongGateway
    ): Flow<List<Song>> {
        return this.observeAllImpl(playlistId)
            .map { mostPlayed ->
                val songList = songGateway.getAll()
                mostPlayed.sortedByDescending { it.timesPlayed }
                    .mapNotNull { item -> songList.find { it.id == item.songId } }
            }
    }

}
