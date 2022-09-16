package dev.olog.data.podcast

import androidx.room.Dao
import androidx.room.Query
import dev.olog.data.mediastore.podcast.MediaStorePodcastsView
import dev.olog.data.mediastore.podcast.MediaStorePodcastsViewSorted
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PodcastDao {

    @Query("SELECT * FROM podcasts_view_sorted")
    // todo made suspend
    abstract fun getAll(): List<MediaStorePodcastsViewSorted>

    @Query("SELECT * FROM podcasts_view_sorted")
    abstract fun observeAll(): Flow<List<MediaStorePodcastsViewSorted>>

    @Query("SELECT * FROM podcasts_view WHERE id = :id")
    // todo made suspend
    abstract fun getById(id: String): MediaStorePodcastsView?

    @Query("SELECT * FROM podcasts_view WHERE id = :id")
    // seems that is not emitting null on missing item
    abstract fun observeById(id: String): Flow<MediaStorePodcastsView?>

    @Query("SELECT * FROM podcasts_view WHERE displayName = :displayName")
    // todo made suspend
    abstract fun getByDisplayName(displayName: String): MediaStorePodcastsView?

    @Query("SELECT * FROM podcasts_view WHERE albumId = :albumId")
    // todo made suspend
    abstract fun getByAlbumId(albumId: String): MediaStorePodcastsView?

    @Query("SELECT COUNT(*) FROM podcasts_view")
    abstract fun countAll(): Int

    @Query("SELECT COUNT(*) FROM podcasts_view")
    abstract fun observeCountAll(): Flow<Int>

}