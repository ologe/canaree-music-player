package dev.olog.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import dev.olog.data.entity.GenreMostPlayedEntity
import dev.olog.data.entity.SongMostTimesPlayedEntity
import dev.olog.domain.entity.Song
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers

@Dao
abstract class GenreMostPlayedDao {

    @Query("SELECT songId, count(*) as timesPlayed " +
            "FROM mostplayedgenre " +
            "WHERE genreId = :genreId " +
            "GROUP BY songId " +
            "HAVING count(*) >= 5 " +
            "ORDER BY timesPlayed DESC " +
            "LIMIT 10")
    abstract fun query(genreId: Long): Flowable<List<SongMostTimesPlayedEntity>>

    @Insert
    abstract fun insertOne(item: GenreMostPlayedEntity)

    fun getAll(genreId: Long, songList: Flowable<List<Song>>): Flowable<List<Song>> {
        return this.query(genreId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMapSingle { it.toFlowable()
                        .flatMapMaybe { song ->
                            songList.flatMapIterable { it }
                                    .filter { it.id == song.songId }
                                    .firstElement()
                                    .map { it.to(song.timesPlayed) }
                        }.toSortedList { (_, time1), (_, time2) -> (time2 - time1) }
                }
                .flatMapSingle { it.toFlowable().map { it.first }.toList() }
    }

}
