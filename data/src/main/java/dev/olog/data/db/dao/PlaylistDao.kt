package dev.olog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.SongGateway
import dev.olog.data.db.entities.PlaylistEntity
import dev.olog.data.db.entities.PlaylistTrackEntity
import dev.olog.shared.assertBackground
import dev.olog.shared.assertBackgroundThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
internal abstract class PlaylistDao {

    @Query("""
        SELECT playlist.*, count(*) as size
        FROM playlist playlist JOIN playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        GROUP BY playlistId
    """)
    abstract fun getAllPlaylists(): List<PlaylistEntity>

    @Query("""
        SELECT playlist.*, count(*) as size
        FROM playlist playlist JOIN playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        GROUP BY playlistId
    """)
    abstract fun observeAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("""
        SELECT playlist.*, count(*) as size
        FROM playlist playlist JOIN playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        where playlist.id = :id
        GROUP BY playlistId
    """)
    abstract fun getPlaylistById(id: Long): PlaylistEntity?

    @Query("""
        SELECT playlist.*, count(*) as size
        FROM playlist playlist JOIN playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        where playlist.id = :id
        GROUP BY playlistId
    """)
    abstract fun observePlaylistById(id: Long): Flow<PlaylistEntity?>

    @Query("""
        SELECT tracks.*
        FROM playlist playlist JOIN playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        WHERE playlistId = :playlistId
        ORDER BY idInPlaylist
    """)
    abstract fun getPlaylistTracksImpl(playlistId: Long): List<PlaylistTrackEntity>

    fun getPlaylistTracks(playlistId: Long, songGateway: SongGateway): List<Song> {
        assertBackgroundThread()
        val trackList = getPlaylistTracksImpl(playlistId)
        val songList : Map<Long, List<Song>> = songGateway.getAll().groupBy { it.id }
        return trackList.mapNotNull { entity ->
            songList[entity.trackId]?.get(0)?.copy(idInPlaylist = entity.idInPlaylist.toInt())
        }
    }

    @Query("""
        SELECT tracks.*
        FROM playlist playlist JOIN playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        WHERE playlistId = :playlistId
        ORDER BY idInPlaylist
    """)
    abstract fun observePlaylistTracksImpl(playlistId: Long): Flow<List<PlaylistTrackEntity>>

    fun observePlaylistTracks(playlistId: Long, songGateway: SongGateway): Flow<List<Song>> {
        return observePlaylistTracksImpl(playlistId)
            .map { trackList ->
                val songList : Map<Long, List<Song>> = songGateway.getAll().groupBy { it.id }
                trackList.mapNotNull { entity ->
                    songList[entity.trackId]?.get(0)?.copy(idInPlaylist = entity.idInPlaylist.toInt())
                }
            }.assertBackground()
    }

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