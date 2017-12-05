package dev.olog.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import dev.olog.data.entity.PlayingQueueEntity
import dev.olog.domain.entity.Song
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*

@Dao
abstract class PlayingQueueDao {

    @Query("SELECT * FROM playing_queue ORDER BY `index`")
    internal abstract fun getAllImpl(): Single<List<PlayingQueueEntity>>

    @Query("DELETE FROM playing_queue")
    internal abstract fun deleteAllImpl()

    @Query("SELECT * FROM playing_queue ORDER BY `index`")
    abstract fun observeAll(): Flowable<List<PlayingQueueEntity>>

    @Insert
    internal abstract fun insertAllImpl(list: List<PlayingQueueEntity>)

    fun getAllAsSongs(songList: Single<List<Song>>): Single<List<Song>> {

        return this.getAllImpl()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flattenAsFlowable { it }
                .map(PlayingQueueEntity::value)
                .flatMapMaybe { songId ->
                    songList.flattenAsFlowable { it }
                            .filter { it.id == songId }
                            .firstElement()
                }.toSortedList { o1, o2 -> String.CASE_INSENSITIVE_ORDER.compare(o1.title, o2.title) }
                .onErrorReturnItem(ArrayList(0))
    }

    @Transaction
    open fun insert(list: List<Long>) {
        deleteAllImpl()
        val result = list.map { PlayingQueueEntity(value = it) }
        insertAllImpl(result)
    }

}