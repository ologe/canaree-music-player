package dev.olog.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.SongGateway
import dev.olog.data.db.entities.GenreMostPlayedEntity
import dev.olog.data.db.entities.SongMostTimesPlayedEntity
import dev.olog.data.utils.asFlow
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
    abstract fun query(genreId: Long): LiveData<List<SongMostTimesPlayedEntity>>

    @Insert
    abstract fun insertOne(item: GenreMostPlayedEntity)

    fun getAll(playlistId: Long, songGateway2: SongGateway): Flow<List<Song>> {
        return this.query(playlistId)
            .asFlow()
            .map { mostPlayed ->
                val songList = songGateway2.getAll()
                mostPlayed.sortedByDescending { it.timesPlayed }
                    .mapNotNull { item -> songList.find { it.id == item.songId } }
            }
    }

}
