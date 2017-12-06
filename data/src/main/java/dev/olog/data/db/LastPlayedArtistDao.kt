package dev.olog.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import dev.olog.data.entity.LastPlayedArtistEntity
import dev.olog.domain.entity.Artist
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

@Dao
abstract class LastPlayedArtistDao {

    @Query("SELECT * FROM last_played_artists ORDER BY dateAdded DESC LIMIT 10")
    abstract fun getAll(): Flowable<List<LastPlayedArtistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertImpl(entity: LastPlayedArtistEntity)

    @Query("DELETE FROM last_played_artists WHERE id = :artistId")
    internal abstract fun deleteImpl(artistId: Long)

    open fun insertOne(artist: Artist) : Completable {
        return Completable.fromCallable{ deleteImpl(artist.id) }
                .andThen { insertImpl(LastPlayedArtistEntity(
                        artist.id, artist.name
                )) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
    }

}
