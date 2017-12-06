package dev.olog.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import dev.olog.data.entity.LastPlayedAlbumEntity
import dev.olog.domain.entity.Album
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

@Dao
abstract class LastPlayedAlbumDao {

    @Query("SELECT * FROM last_played_albums ORDER BY dateAdded DESC LIMIT 10")
    abstract fun getAll(): Flowable<List<LastPlayedAlbumEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertImpl(entity: LastPlayedAlbumEntity)

    @Query("DELETE FROM last_played_albums WHERE id = :albumId")
    internal abstract fun deleteImpl(albumId: Long)

    open fun insertOne(album: Album) : Completable {
        return Completable.fromCallable{ deleteImpl(album.id) }
                .andThen { insertImpl(LastPlayedAlbumEntity(
                        album.id, album.artistId, album.title, album.artist, album.image
                )) }
                .subscribeOn(Schedulers.io())
    }

}