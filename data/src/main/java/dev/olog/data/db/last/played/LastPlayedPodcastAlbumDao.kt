package dev.olog.data.db.last.played

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LastPlayedPodcastAlbumDao {

    @Query(
        """
        SELECT * FROM last_played_podcast_albums
        ORDER BY dateAdded DESC
        LIMIT 10
    """
    )
    abstract fun getAll(): Flow<List<LastPlayedPodcastAlbumEntity>>

    @Insert
    internal abstract suspend fun insertImpl(entity: LastPlayedPodcastAlbumEntity)

    @Query(
        """
        DELETE FROM last_played_podcast_albums
        WHERE id = :albumId
    """
    )
    internal abstract suspend fun deleteImpl(albumId: Long)

    @Transaction
    open suspend fun insertOne(id: Long) {
        deleteImpl(id)
        insertImpl(LastPlayedPodcastAlbumEntity(id))
    }

}