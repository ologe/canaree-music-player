package dev.olog.data.mediastore.song.genre

import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class MediaStoreGenresViewDao {

    @Query("SELECT * FROM genres_view")
    abstract suspend fun getAll(): List<MediaStoreGenresView>

    @Query("SELECT * FROM genres_view_sorted")
    abstract suspend fun getAllSorted(): List<MediaStoreGenresViewSorted>

}