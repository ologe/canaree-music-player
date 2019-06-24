package dev.olog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.SongGateway2
import dev.olog.data.db.entities.GenreMostPlayedEntity
import dev.olog.data.db.entities.SongMostTimesPlayedEntity
import io.reactivex.Flowable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.flow.asFlow

@Dao
abstract class GenreMostPlayedDao {

    @Query("""
        SELECT songId, count(*) as timesPlayed
        FROM most_played_genre
        WHERE genreId = :genreId
        GROUP BY songId
        HAVING count(*) >= 5
        ORDER BY timesPlayed DESC
        LIMIT 10
    """)
    abstract fun query(genreId: Long): Flowable<List<SongMostTimesPlayedEntity>>

    @Insert
    abstract fun insertOne(item: GenreMostPlayedEntity)

    fun getAll(playlistId: Long, songGateway2: SongGateway2): Flow<List<Song>> {
        return this.query(playlistId)
                .map { mostPlayed ->
                    val songList = songGateway2.getAll()
                    mostPlayed.sortedByDescending { it.timesPlayed }
                            .mapNotNull { item -> songList.find { it.id == item.songId } }
                }.asFlow()
    }

}
