package dev.olog.msc.data.db

import android.arch.persistence.room.*
import dev.olog.msc.data.entity.FavoriteEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
abstract class FavoriteDao {

    @Query("SELECT songId FROM favorite_songs")
    internal abstract fun getAllImpl(): Flowable<List<Long>>

    @Query("DELETE FROM favorite_songs")
    abstract fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertOneImpl(item: FavoriteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertGroupImpl(item: List<FavoriteEntity>)

    @Delete
    internal abstract fun deleteGroupImpl(item: List<FavoriteEntity>)

    @Transaction
    open fun addToFavoriteSingle(id: Long): Completable {
        return Completable.fromCallable { insertOneImpl(FavoriteEntity(id)) }
    }

    @Transaction
    open fun addToFavorite(songIds: List<Long>): Completable {
        return Completable.fromCallable {
            insertGroupImpl(songIds.map { FavoriteEntity(it) })
        }
    }

    open fun removeFromFavorite(songId: List<Long>): Completable {
        return Single.fromCallable { songId.map { FavoriteEntity(it) } }
                .map { deleteGroupImpl(it) }
                .toCompletable()
    }

    @Query("SELECT songId FROM favorite_songs WHERE songId = :songId LIMIT 1")
    abstract fun isFavorite(songId: Long): FavoriteEntity?

}