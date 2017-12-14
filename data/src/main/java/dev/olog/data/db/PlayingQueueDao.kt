package dev.olog.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import dev.olog.data.entity.PlayingQueueEntity
import dev.olog.domain.entity.Song
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.groupMap
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

@Dao
abstract class PlayingQueueDao {

    @Query("SELECT * FROM playing_queue ORDER BY progressive")
    internal abstract fun getAllImpl(): Flowable<List<PlayingQueueEntity>>

    @Query("DELETE FROM playing_queue")
    internal abstract fun deleteAllImpl()

    @Insert
    internal abstract fun insertAllImpl(list: List<PlayingQueueEntity>)

    fun getAllAsSongs(songList: Single<List<Song>>): Flowable<List<Song>> {

        return this.getAllImpl()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .groupMap { it.songId }
                .flatMapSingle { ids -> songList.flatMap { songs ->
                    val result : List<Song> = ids.asSequence()
                            .map { id -> songs.firstOrNull { it.id == id } }
                            .filter { it != null }
                            .map { it!! }
                            .toList()
                    Single.just(result)
                } }
    }

    fun insert(list: List<Pair<String, Long>>) : Completable {
        return Single.fromCallable { deleteAllImpl() }
                .map { list.map { PlayingQueueEntity(songId = it.second,
                        category = MediaIdHelper.extractCategory(it.first),
                        categoryValue = MediaIdHelper.extractCategoryValue(it.first)) }
                }.flatMapCompletable { queueList -> CompletableSource { insertAllImpl(queueList) } }
    }

}