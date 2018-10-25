package dev.olog.msc.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.msc.data.entity.FolderMostPlayedEntity
import dev.olog.msc.data.entity.SongMostTimesPlayedEntity
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.utils.k.extension.mapToList
import io.reactivex.Flowable
import io.reactivex.Observable

@Dao
abstract class FolderMostPlayedDao {

    @Query("""
        SELECT songId, count(*) as timesPlayed
        FROM most_played_folder
        WHERE folderPath = :folderPath
        GROUP BY songId
        HAVING count(*) >= 5
        ORDER BY timesPlayed DESC
        LIMIT 10
    """)
    abstract fun query(folderPath: String): Flowable<List<SongMostTimesPlayedEntity>>

    @Insert
    abstract fun insertOne(item: FolderMostPlayedEntity)

    fun getAll(folderPath: String, songList: Observable<List<Song>>): Observable<List<Song>> {
        return this.query(folderPath)
                .toObservable()
                .switchMap { mostPlayedSongs -> songList.map { songList ->
                    mostPlayedSongs.mapNotNull { mostPlayed ->
                        val song = songList.firstOrNull { it.id == mostPlayed.songId }
                        if (song != null) song to mostPlayed.timesPlayed
                        else null
                    }.sortedWith(compareByDescending { it.second })
                    }.mapToList { it.first }
                }
    }

}
