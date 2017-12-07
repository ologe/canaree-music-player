package dev.olog.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import dev.olog.data.entity.HistoryEntity
import dev.olog.domain.entity.Song
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers

@Dao
abstract class HistoryDao {


    @Query("SELECT * FROM song_history ORDER BY dateAdded DESC LIMIT 200")
    internal abstract fun getAllImpl(): Flowable<List<HistoryEntity>>

    fun getAllAsSongs(songList: Single<List<Song>>): Flowable<List<Song>> {
        return getAllImpl()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .flatMapSingle { it.toFlowable()
                        .map(HistoryEntity::songId)
                        .flatMapMaybe { songId ->
                            songList.flattenAsFlowable { it }
                                    .filter { (id) -> id == songId }
                                    .firstElement()
                        }.toList()
                }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertImpl(entity: HistoryEntity)

//    @Query("DELETE FROM song_history WHERE songId = :id")
//    abstract fun deleteImpl(id: Long)

    fun insert(id: Long): Completable {
//        return Completable.fromCallable{ deleteImpl(id) }
//                .andThen { insertImpl(HistoryEntity.from(id)) }
//                .subscribeOn(Schedulers.io())
        return Completable.fromCallable{ insertImpl(HistoryEntity(songId = id)) }
                .subscribeOn(Schedulers.io())
    }

}
