package dev.olog.msc.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import dev.olog.msc.data.entity.PlaylistMostPlayedEntity
import dev.olog.msc.data.entity.SongMostTimesPlayedEntity
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.utils.k.extension.mapToList
import io.reactivex.Flowable
import io.reactivex.Observable

@Dao
abstract class PlaylistMostPlayedDao {

    @Query("SELECT songId, count(*) as timesPlayed " +
            "FROM mostplayedplaylist " +
            "WHERE playlistId = :playlistId " +
            "GROUP BY songId " +
            "HAVING count(*) >= 5 " +
            "ORDER BY timesPlayed DESC " +
            "LIMIT 10")
    abstract fun query(playlistId: Long): Flowable<List<SongMostTimesPlayedEntity>>

    @Insert
    abstract fun insertOne(item: PlaylistMostPlayedEntity)

    fun getAll(playlistId: Long, songList: Observable<List<Song>>): Observable<List<Song>> {
        return this.query(playlistId)
                .toObservable()
                .flatMap { mostPlayedSongs -> songList.map { songList ->
                    mostPlayedSongs.mapNotNull { mostPlayed ->
                        val song = songList.firstOrNull { it.id == mostPlayed.songId }
                        if (song != null) song to mostPlayed.timesPlayed
                        else null
                    }.sortedWith(compareByDescending { it.second })
                }.mapToList { it.first }
                }
    }

}
