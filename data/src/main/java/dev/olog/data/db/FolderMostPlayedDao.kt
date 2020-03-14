package dev.olog.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.TrackGateway
import dev.olog.data.model.db.FolderMostPlayedEntity
import dev.olog.data.model.db.MostTimesPlayedSongEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
internal abstract class FolderMostPlayedDao {

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
    abstract fun query(folderPath: String): Flow<List<MostTimesPlayedSongEntity>>

    @Insert
    abstract suspend fun insert(vararg item: FolderMostPlayedEntity)

    fun observeAll(folderPath: String, trackGateway: TrackGateway): Flow<List<Song>> {
        return this.query(folderPath)
            .map { mostPlayed ->
                val songList = trackGateway.getAllTracks()
                mostPlayed.sortedByDescending { it.timesPlayed }
                    .mapNotNull { item -> songList.find { it.id == item.songId } }
            }
    }

}
