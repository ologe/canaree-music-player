package dev.olog.data.mediastore.song.folder

import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class MediaStoreFoldersViewDao {

    @Query("SELECT * FROM folders_view")
    abstract suspend fun getAll(): List<MediaStoreFoldersView>

    @Query("SELECT * FROM folders_view_sorted")
    abstract suspend fun getAllSorted(): List<MediaStoreFoldersViewSorted>

}