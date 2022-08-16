package dev.olog.data.mediastore.podcast.artist

import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class MediaStorePodcastArtistsViewDao {

    @Query("SELECT * FROM podcast_artists_view")
    abstract suspend fun getAll(): List<MediaStorePodcastArtistsView>

    @Query("SELECT * FROM podcast_artists_view_sorted")
    abstract suspend fun getAllSorted(): List<MediaStorePodcastArtistsViewSorted>

}