package dev.olog.msc.data.db

import android.arch.persistence.room.*
import dev.olog.msc.data.entity.FavoriteEntity
import dev.olog.msc.domain.entity.Song
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

@Dao
abstract class FavoriteDao {

    @Query("SELECT songId FROM favorite_songs")
    internal abstract fun getAllImpl(): Flowable<List<Long>>

    @Query("DELETE FROM favorite_songs")
    abstract fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertGroupImpl(item: List<FavoriteEntity>)

    @Delete
    internal abstract fun deleteGroupImpl(item: List<FavoriteEntity>)

    @Transaction
    open fun addToFavoriteSingle(song: Song): Single<String> {
        return Single.create<String> { e ->

            val result = FavoriteEntity(song.id)
            insertGroupImpl(listOf(result))

            e.onSuccess(song.title)
        }
    }

    @Transaction
    open fun addToFavorite(songIds: List<Long>): Single<String> {
        return Single.create<String> { e ->

            val result = songIds.map { FavoriteEntity(it) }
            insertGroupImpl(result)

            e.onSuccess(result.size.toString())
        }
    }

    open fun removeFromFavorite(songId: List<Long>): Completable {
        return Observable.fromIterable(songId)
                .observeOn(Schedulers.io())
                .map { FavoriteEntity(it) }
                .toList()
                .flatMap { Single
                        .fromCallable { deleteGroupImpl(it) }
                        .subscribeOn(Schedulers.io())
                }.toCompletable()
    }

    @Query("SELECT songId FROM favorite_songs WHERE songId = :songId LIMIT 1")
    abstract fun isFavorite(songId: Long): FavoriteEntity?

}