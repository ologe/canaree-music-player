package dev.olog.msc.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import dev.olog.msc.data.entity.LastPlayedAlbumEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

@Dao
abstract class LastPlayedAlbumDao {

    @Query("SELECT * FROM last_played_albums ORDER BY dateAdded DESC LIMIT 10")
    abstract fun getAll(): Flowable<List<LastPlayedAlbumEntity>>

    @Insert
    internal abstract fun insertImpl(entity: LastPlayedAlbumEntity)

    @Query("DELETE FROM last_played_albums WHERE id = :albumId")
    internal abstract fun deleteImpl(albumId: Long)

    @Transaction
    open fun insertOne(id: Long) : Completable {
        return Completable
                .fromCallable{ deleteImpl(id) }
                .andThen { insertImpl(LastPlayedAlbumEntity(id)) }
                .subscribeOn(Schedulers.io())
    }

}