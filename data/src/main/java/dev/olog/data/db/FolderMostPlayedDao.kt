package dev.olog.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import dev.olog.data.entity.FolderMostPlayedEntity
import dev.olog.data.model.SongMostTimesPlayedEntity
import dev.olog.domain.entity.Song
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers

@Dao
abstract class FolderMostPlayedDao {

    @Query("SELECT songId, count(*) as timesPlayed " +
            "FROM mostplayedfolder " +
            "WHERE folderPath = :folderPath " +
            "GROUP BY songId " +
            "HAVING count(*) >= 5 " +
            "ORDER BY timesPlayed DESC " +
            "LIMIT 10")
    abstract fun query(folderPath: String): Flowable<List<SongMostTimesPlayedEntity>>

    @Insert
    abstract fun insertOne(item: FolderMostPlayedEntity)

    fun getAll(folderPath: String, songList: Flowable<List<Song>>): Flowable<List<Song>> {
        return this.query(folderPath)
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
