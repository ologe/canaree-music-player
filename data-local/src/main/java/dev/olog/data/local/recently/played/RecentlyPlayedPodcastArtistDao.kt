package dev.olog.data.local.recently.played

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RecentlyPlayedPodcastArtistDao {

    @Query(
        """
        SELECT * FROM last_played_podcast_artists
        ORDER BY dateAdded DESC
        LIMIT 20
    """
    )
    abstract fun observeAll(): Flow<List<RecentlyPlayedPodcastArtistEntity>>

    @Insert
    internal abstract suspend fun insertImpl(entity: RecentlyPlayedPodcastArtistEntity)

    @Query(
        """
        DELETE FROM last_played_podcast_artists
        WHERE id = :artistId
    """
    )
    internal abstract suspend fun deleteImpl(artistId: Long)

    @Transaction
    open suspend fun insertOne(id: Long) {
        deleteImpl(id)
        insertImpl(RecentlyPlayedPodcastArtistEntity(id))
    }

}
