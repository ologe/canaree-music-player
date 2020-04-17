package dev.olog.data.spotify.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.data.spotify.entity.GeneratedPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class GeneratedPlaylistsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun createPlaylists(playlists: List<GeneratedPlaylistEntity>)

    @Query("SELECT * FROM generated_playlist")
    abstract fun observePlaylists(): Flow<List<GeneratedPlaylistEntity>>

    @Query("SELECT * FROM generated_playlist WHERE playlistId = :id")
    abstract fun getPlaylistById(id: Long): GeneratedPlaylistEntity

    @Query("SELECT * FROM generated_playlist WHERE playlistId = :id")
    abstract fun observePlaylistById(id: Long): Flow<GeneratedPlaylistEntity>

    @Query("DELETE FROM generated_playlist")
    abstract fun clearPlaylists()

}