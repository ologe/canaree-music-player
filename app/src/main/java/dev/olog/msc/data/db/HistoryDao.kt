package dev.olog.msc.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import dev.olog.msc.data.entity.HistoryEntity
import dev.olog.msc.domain.entity.Song
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

@Dao
abstract class HistoryDao {


    @Query("SELECT * FROM song_history ORDER BY dateAdded DESC LIMIT 200")
    internal abstract fun getAllImpl(): Flowable<List<HistoryEntity>>

    @Query("DELETE FROM song_history")
    abstract fun deleteAll()

    @Query("DELETE FROM song_history WHERE id = :songId")
    abstract fun deleteSingle(songId: Long)

    fun getAllAsSongs(songList: Single<List<Song>>): Flowable<List<Song>> {
        return getAllImpl()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .flatMapSingle { ids -> songList.flatMap { songs ->
                    val result : List<Song> = ids.asSequence()
                            .map { historyEntity ->
                                val song = songs.firstOrNull { it.id == historyEntity.songId }
                                song?.copy(trackNumber = historyEntity.id)
                            }
                            .filter { it != null }
                            .map { it!! }
                            .toList()
                    Single.just(result)
                } }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertImpl(entity: HistoryEntity)

    fun insert(id: Long): Completable {
        return Completable.fromCallable{ insertImpl(HistoryEntity(songId = id)) }
                .subscribeOn(Schedulers.io())
    }

}
