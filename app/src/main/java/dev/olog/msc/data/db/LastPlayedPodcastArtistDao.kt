package dev.olog.msc.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
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
