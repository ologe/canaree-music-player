package dev.olog.msc.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import dev.olog.msc.data.entity.PodcastPlaylist
import dev.olog.msc.data.entity.PodcastPlaylistTrack
import io.reactivex.Flowable

@Dao
abstract class PodcastPlaylistDao {

    @Query("""
        SELECT playlist.*, count(*) as size
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        GROUP BY playlistId
    """)
    abstract fun getAllPlaylists(): Flowable<List<PodcastPlaylist>>

    @Query("""
        SELECT playlist.*, count(*) as size
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        GROUP BY playlistId
    """)
    abstract fun getAllPlaylistsBlocking(): List<PodcastPlaylist>

    @Query("""
        SELECT playlist.*, count(*) as size
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        where playlist.id = :id
        GROUP BY playlistId
    """)
    abstract fun getPlaylist(id: Long): Flowable<PodcastPlaylist>

    @Query("""
        SELECT tracks.*
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        WHERE playlistId = :playlistId
    """)
    abstract fun getPlaylistTracks(playlistId: Long): Flowable<List<PodcastPlaylistTrack>>

    @Insert
    abstract fun createPlaylist(playlist: PodcastPlaylist): Long

    @Query("""
        UPDATE podcast_playlist SET name = :name WHERE id = :id
    """)
    abstract fun renamePlaylist(id: Long, name: String)

    @Query("""DELETE FROM podcast_playlist WHERE id = :id""")
    abstract fun deletePlaylist(id: Long)

    @Insert
    abstract fun insertTracks(tracks: List<PodcastPlaylistTrack>)

    @Query("""
        DELETE FROM podcast_playlist_tracks
        WHERE playlistId = :playlistId AND id = :idInPlaylist
    """)
    abstract fun deleteTrack(playlistId: Long, idInPlaylist: Long)

    @Query("""
        DELETE FROM podcast_playlist_tracks WHERE playlistId = :id
    """)
    abstract fun clearPlaylist(id: Long)

    @Query("""
        DELETE FROM podcast_playlist_tracks
        WHERE EXISTS (
            SELECT count(*) as items
            FROM podcast_playlist_tracks
            WHERE playlistId = :id
            GROUP BY id, playlistId
            HAVING items > 1
        )
    """)
    abstract fun removeDuplicated(id: Long)

}