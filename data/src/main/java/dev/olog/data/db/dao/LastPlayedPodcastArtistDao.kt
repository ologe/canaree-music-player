package dev.olog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.data.db.entities.LastPlayedPodcastArtistEntity
import dev.olog.data.mediastore.artist.MediaStoreArtistEntity
import dev.olog.data.queries.QueryUtils
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LastPlayedPodcastArtistDao {

    @Query("""
        SELECT mediastore_artists.*
        FROM last_played_podcast_artists JOIN mediastore_artists
            ON last_played_podcast_artists.id = mediastore_artists.artist_id
        ORDER BY last_played_podcast_artists.dateAdded DESC
        LIMIT ${QueryUtils.LAST_PLAYED_MAX_ITEM_TO_SHOW}
    """)
    abstract fun observeAll(): Flow<List<MediaStoreArtistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract suspend fun insertOne(entity: LastPlayedPodcastArtistEntity)

}
