package dev.olog.data.mediastore.song.album

import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class MediaStoreAlbumsViewDao {

    @Query("SELECT * FROM albums_view")
    abstract suspend fun getAll(): List<MediaStoreAlbumsView>

    @Query("SELECT * FROM albums_view_sorted")
    abstract suspend fun getAllSorted(): List<MediaStoreAlbumsViewSorted>

}