package dev.olog.data.db

import android.arch.persistence.room.*
import dev.olog.data.entity.FavoriteEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

@Dao
abstract class FavoriteDao {

    @Query("SELECT songId FROM favorite_songs")
    internal abstract fun getAllImpl(): Flowable<List<Long>>

    @Insert
    internal abstract fun insertGroupImpl(item: List<FavoriteEntity>)

    @Delete
    internal abstract fun deleteGroupImpl(item: List<FavoriteEntity>)

    @Transaction
    open fun addToFavorite(songId: List<Long>): Completable {
        return Flowable.fromIterable(songId)
                .observeOn(Schedulers.io())
                .map { FavoriteEntity(it) }
                .toList()
                .doOnSuccess { insertGroupImpl(it) }
                .toCompletable()
    }

    @Transaction
    open fun removeFromFavorite(songId: List<Long>): Completable {
        return Flowable.fromIterable(songId)
                .observeOn(Schedulers.io())
                .map { FavoriteEntity(it) }
                .toList()
                .doOnSuccess { deleteGroupImpl(it) }
                .toCompletable()
    }

    @Query("SELECT songId FROM favorite_songs WHERE songId = :songId LIMIT 1")
    abstract fun isFavorite(songId: Long): FavoriteEntity?

}