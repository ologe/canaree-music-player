package dev.olog.msc.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.msc.data.entity.LastPlayedAlbumEntity
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
abstract class LastPlayedAlbumDao {

    @Query("""
        SELECT * FROM last_played_albums
        ORDER BY dateAdded DESC
        LIMIT 10
    """)
    abstract fun getAll(): Flowable<List<LastPlayedAlbumEntity>>

    @Insert
    internal abstract fun insertImpl(entity: LastPlayedAlbumEntity)

    @Query("""
        DELETE FROM last_played_albums
        WHERE id = :albumId
    """)
    internal abstract fun deleteImpl(albumId: Long)

    fun insertOne(id: Long) : Completable {
        return Completable.fromCallable{ deleteImpl(id) }
                .andThen { insertImpl(LastPlayedAlbumEntity(id)) }
    }

}