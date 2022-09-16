package dev.olog.data.mediastore.song.playlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class MediaStorePlaylistDao {

    @Query("SELECT * FROM mediastore_playlist")
    abstract suspend fun getAllPlaylists(): List<MediaStorePlaylistEntity>

    @Query("DELETE FROM mediastore_playlist")
    abstract suspend fun deleteAllPlaylists()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAllPlaylists(items: List<MediaStorePlaylistEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAllPlaylists(vararg items: MediaStorePlaylistEntity)

    @Query("SELECT * FROM mediastore_playlist_track")
    abstract suspend fun getAllPlaylistTracks(): List<MediaStorePlaylistTrackEntity>

    @Query("DELETE FROM mediastore_playlist_track")
    abstract suspend fun deleteAllPlaylistTracks()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAllPlaylistTracks(items: List<MediaStorePlaylistTrackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAllPlaylistTracks(vararg items: MediaStorePlaylistTrackEntity)

    @Transaction
    open suspend fun replaceAll(
        playlists: List<MediaStorePlaylistEntity>,
        playlistTracks: List<MediaStorePlaylistTrackEntity>,
    ) {
        deleteAllPlaylists()
        deleteAllPlaylistTracks()
        insertAllPlaylists(playlists)
        insertAllPlaylistTracks(playlistTracks)
    }
    
}