package dev.olog.data.mediastore

import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class MediaStoreSongViewDao {

    @Query("SELECT * FROM songs_view")
    abstract suspend fun getAll(): List<MediaStoreSongView>

    @Query("SELECT * FROM sorted_songs_view")
    abstract suspend fun getSortedAll(): List<MediaStoreSortedSongView>

}