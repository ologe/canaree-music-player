package dev.olog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.data.db.entities.LastPlayedArtistEntity
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
internal abstract class LastPlayedArtistDao {

    @Query("""
        SELECT * FROM last_played_artists
        ORDER BY dateAdded DESC
        LIMIT 20
    """)
    abstract fun getAll(): Flowable<List<LastPlayedArtistEntity>>

    @Insert
    internal abstract fun insertImpl(entity: LastPlayedArtistEntity)

    @Query("""
        DELETE FROM last_played_artists
        WHERE id = :artistId
    """)
    internal abstract fun deleteImpl(artistId: Long)

    fun insertOne(id: Long) : Completable {
        return Completable.fromCallable{ deleteImpl(id) }
                .andThen { insertImpl(LastPlayedArtistEntity(id)) }
    }

}
