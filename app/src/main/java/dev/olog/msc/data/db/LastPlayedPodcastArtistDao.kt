package dev.olog.msc.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import dev.olog.msc.data.entity.LastPlayedPodcastArtistEntity
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
abstract class LastPlayedPodcastArtistDao {

    @Query("""
        SELECT * FROM last_played_podcast_artists
        ORDER BY dateAdded DESC
        LIMIT 20
    """)
    abstract fun getAll(): Flowable<List<LastPlayedPodcastArtistEntity>>

    @Insert
    internal abstract fun insertImpl(entity: LastPlayedPodcastArtistEntity)

    @Query("""
        DELETE FROM last_played_podcast_artists
        WHERE id = :artistId
    """)
    internal abstract fun deleteImpl(artistId: Long)

    fun insertOne(id: Long) : Completable {
        return Completable.fromCallable{ deleteImpl(id) }
                .andThen { insertImpl(LastPlayedPodcastArtistEntity(id)) }
    }

}
