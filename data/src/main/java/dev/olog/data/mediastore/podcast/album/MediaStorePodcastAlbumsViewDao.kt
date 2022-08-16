package dev.olog.data.mediastore.podcast.album

import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class MediaStorePodcastAlbumsViewDao {

    @Query("SELECT * FROM podcast_albums_view")
    abstract suspend fun getAll(): List<MediaStorePodcastAlbumsView>

    @Query("SELECT * FROM podcast_albums_view_sorted")
    abstract suspend fun getAllSorted(): List<MediaStorePodcastAlbumsViewSorted>

}