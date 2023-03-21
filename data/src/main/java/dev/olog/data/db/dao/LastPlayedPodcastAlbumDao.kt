package dev.olog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.data.db.entities.LastPlayedPodcastAlbumEntity
import dev.olog.data.mediastore.album.MediaStoreAlbumEntity
import dev.olog.data.queries.QueryUtils
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LastPlayedPodcastAlbumDao {

    @Query("""
        SELECT mediastore_albums.*
        FROM last_played_podcast_albums JOIN mediastore_albums
            ON last_played_podcast_albums.id = mediastore_albums.album_id
        ORDER BY last_played_podcast_albums.dateAdded DESC
        LIMIT ${QueryUtils.LAST_PLAYED_MAX_ITEM_TO_SHOW}
    """)
    abstract fun observeAll(): Flow<List<MediaStoreAlbumEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertOne(entity: LastPlayedPodcastAlbumEntity)

}