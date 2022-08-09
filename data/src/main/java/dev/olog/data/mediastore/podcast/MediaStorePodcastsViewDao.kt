package dev.olog.data.mediastore.podcast

import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class MediaStorePodcastsViewDao {

    @Query("SELECT * FROM podcasts_view")
    abstract suspend fun getAll(): List<MediaStorePodcastsView>

    @Query("SELECT * FROM podcasts_view_sorted")
    abstract suspend fun getAllSorted(): List<MediaStorePodcastsViewSorted>

}