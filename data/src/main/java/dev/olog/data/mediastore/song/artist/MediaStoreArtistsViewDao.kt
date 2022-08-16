package dev.olog.data.mediastore.song.artist

import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class MediaStoreArtistsViewDao {

    @Query("SELECT * FROM artists_view")
    abstract suspend fun getAll(): List<MediaStoreArtistsView>

    @Query("SELECT * FROM artists_view_sorted")
    abstract suspend fun getAllSorted(): List<MediaStoreArtistsViewSorted>

}