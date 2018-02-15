package dev.olog.msc.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import dev.olog.msc.data.entity.LastPlayedArtistEntity
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
abstract class LastPlayedArtistDao {

    @Query("SELECT * FROM last_played_artists ORDER BY dateAdded DESC LIMIT 20")
    abstract fun getAll(): Flowable<List<LastPlayedArtistEntity>>

    @Insert
    internal abstract fun insertImpl(entity: LastPlayedArtistEntity)

    @Query("DELETE FROM last_played_artists WHERE id = :artistId")
    internal abstract fun deleteImpl(artistId: Long)

    fun insertOne(id: Long) : Completable {
        return Completable.fromCallable{ deleteImpl(id) }
                .andThen { insertImpl(LastPlayedArtistEntity(id)) }
    }

}
