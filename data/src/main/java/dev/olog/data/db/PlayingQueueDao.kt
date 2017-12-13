package dev.olog.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import dev.olog.data.entity.PlayingQueueEntity
import dev.olog.domain.entity.Song
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers
import java.util.*

@Dao
abstract class PlayingQueueDao {

    @Query("SELECT * FROM playing_queue ORDER BY timeAdded DESC")
    internal abstract fun getAllImpl(): Flowable<List<PlayingQueueEntity>>

    @Query("DELETE FROM playing_queue")
    internal abstract fun deleteAllImpl()

    @Insert
    internal abstract fun insertAllImpl(list: List<PlayingQueueEntity>)

    fun getAllAsSongs(songList: Single<List<Song>>): Flowable<List<Song>> {

        return this.getAllImpl()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMapSingle { it.toFlowable()
                        .flatMapMaybe { entity ->
                            songList.flattenAsFlowable { it }
                                    .filter { it.id == entity.songId }
                                    .firstElement()
                        }.toList()
                        .onErrorReturnItem(ArrayList(0))
                }
    }

    fun insert(list: List<Long>) : Completable {
        return Single.fromCallable { deleteAllImpl() }
                .map { list.map { PlayingQueueEntity(songId = it) } }
                .flatMapCompletable { queueList -> CompletableSource { insertAllImpl(queueList) } }
    }

}