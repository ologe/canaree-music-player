package dev.olog.data.song.playlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
@Deprecated("")
abstract class LegacyPlaylistDao {

    @Query("""
        SELECT tracks.*
        FROM playlist playlist JOIN playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        WHERE playlistId = :playlistId
        ORDER BY idInPlaylist
    """)
    abstract fun getPlaylistTracksImpl(playlistId: Long): List<PlaylistTrackEntity>

    @Query("""
        SELECT tracks.*
        FROM playlist playlist JOIN playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        WHERE playlistId = :playlistId
        ORDER BY idInPlaylist
    """)
    abstract fun observePlaylistTracksImpl(playlistId: Long): Flow<List<PlaylistTrackEntity>>

    @Query("""
        SELECT max(idInPlaylist)
        FROM playlist playlist JOIN playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        WHERE playlistId = :playlistId
    """)
    abstract suspend fun getPlaylistMaxId(playlistId: Long): Int?

    @Insert
    abstract fun createPlaylist(playlist: PlaylistEntity): Long

    @Query("""
        UPDATE playlist SET name = :name WHERE id = :id
    """)
    abstract suspend fun renamePlaylist(id: Long, name: String)

    @Query("""DELETE FROM playlist WHERE id = :id""")
    abstract suspend fun deletePlaylist(id: Long)

    @Insert
    abstract fun insertTracks(tracks: List<PlaylistTrackEntity>)

    @Query("""
        DELETE FROM playlist_tracks
        WHERE playlistId = :playlistId AND idInPlaylist = :idInPlaylist
    """)
    abstract suspend fun deleteTrack(playlistId: Long, idInPlaylist: Long)

    @Query("""
        DELETE FROM playlist_tracks
        WHERE playlistId = :playlistId
    """)
    abstract suspend fun deletePlaylistTracks(playlistId: Long)

    @Query("""
        DELETE FROM playlist_tracks WHERE playlistId = :id
    """)
    abstract suspend fun clearPlaylist(id: Long)

    @Update
    abstract suspend fun updateTrackList(list: List<PlaylistTrackEntity>)

}