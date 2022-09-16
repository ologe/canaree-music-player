package dev.olog.data.song.playlist

import androidx.room.Dao
import androidx.room.Query
import dev.olog.data.mediastore.song.playlist.MediaStorePlaylistsView
import dev.olog.data.mediastore.song.playlist.MediaStorePlaylistsViewSorted
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PlaylistDao {

    @Query("SELECT * from playlists_view_sorted")
    // todo made suspend
    abstract fun getAll(): List<MediaStorePlaylistsViewSorted>

    @Query("SELECT * from playlists_view_sorted")
    abstract fun observeAll(): Flow<List<MediaStorePlaylistsViewSorted>>

    @Query("SELECT * from playlists_view WHERE id = :id")
    // todo made suspend
    abstract fun getById(id: String): MediaStorePlaylistsView?

    @Query("SELECT * from playlists_view WHERE id = :id")
    abstract fun observeById(id: String): Flow<MediaStorePlaylistsView?>
    
}