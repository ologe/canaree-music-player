package dev.olog.data.mediastore.song

import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class MediaStoreSongsViewDao {

    @Query("SELECT * FROM songs_view")
    abstract suspend fun getAll(): List<MediaStoreSongsView>

    @Query("SELECT * FROM songs_view_sorted")
    abstract suspend fun getAllSorted(): List<MediaStoreSongsViewSorted>

}