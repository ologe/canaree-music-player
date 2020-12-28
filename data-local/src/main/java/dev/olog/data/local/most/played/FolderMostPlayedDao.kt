package dev.olog.data.local.most.played

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.core.entity.track.Track
import dev.olog.core.gateway.track.SongGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
abstract class FolderMostPlayedDao {

    @Query(
        """
        SELECT songId, count(*) as timesPlayed
        FROM most_played_folder
        WHERE folderPath = :folderPath
        GROUP BY songId
        HAVING count(*) >= 5
        ORDER BY timesPlayed DESC
        LIMIT 10
    """
    )
    abstract fun observeAllImpl(folderPath: String): Flow<List<SongMostTimesPlayedEntity>>

    @Insert
    abstract suspend fun insertOne(item: FolderMostPlayedEntity)

    fun observeAll(
        folderPath: String,
        songGateway: SongGateway
    ): Flow<List<Track>> {
        return this.observeAllImpl(folderPath)
            .map { mostPlayed ->
                val songList = songGateway.getAll()
                mostPlayed.sortedByDescending { it.timesPlayed }
                    .mapNotNull { item -> songList.find { it.id == item.songId } }
            }
    }

}
