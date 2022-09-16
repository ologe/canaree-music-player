package dev.olog.data.mediastore.song.playlist

import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class MediaStorePlaylistsViewDao {

    @Query("SELECT * FROM playlists_view")
    abstract suspend fun getAll(): List<MediaStorePlaylistsView>

    @Query("SELECT * FROM playlists_view_sorted")
    abstract suspend fun getAllSorted(): List<MediaStorePlaylistsView>

}