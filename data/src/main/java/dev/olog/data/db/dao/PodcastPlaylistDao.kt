package dev.olog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.data.db.entities.PodcastPlaylistEntity
import dev.olog.data.db.entities.PodcastPlaylistTrackEntity
import dev.olog.data.utils.assertBackground
import dev.olog.data.utils.assertBackgroundThread
import io.reactivex.Flowable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.flow.asFlow

@Dao
internal abstract class PodcastPlaylistDao {

    @Query("""
        SELECT playlist.*, count(*) as size
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        GROUP BY playlistId
    """)
    abstract fun getAllPlaylists(): List<PodcastPlaylistEntity>

    @Query("""
        SELECT playlist.*, count(*) as size
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        GROUP BY playlistId
    """)
    abstract fun observeAllPlaylists(): Flowable<List<PodcastPlaylistEntity>>

    @Query("""
        SELECT playlist.*, count(*) as size
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        where playlist.id = :id
        GROUP BY playlistId
    """)
    abstract fun getPlaylistById(id: Long): PodcastPlaylistEntity?

    @Query("""
        SELECT playlist.*, count(*) as size
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        where playlist.id = :id
        GROUP BY playlistId
    """)
    abstract fun observePlaylistById(id: Long): Flowable<PodcastPlaylistEntity?>

    @Query("""
        SELECT tracks.*
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        WHERE playlistId = :playlistId
        ORDER BY idInPlaylist
    """)
    abstract fun getPlaylistTracksImpl(playlistId: Long): List<PodcastPlaylistTrackEntity>

    fun getPlaylistTracks(playlistId: Long, podcastGateway: PodcastGateway): List<Song> {
        assertBackgroundThread()
        val trackList = getPlaylistTracksImpl(playlistId)
        val songList : Map<Long, List<Song>> = podcastGateway.getAll().groupBy { it.id }
        return trackList.mapNotNull { entity ->
            songList[entity.podcastId]?.get(0)?.copy(idInPlaylist = entity.idInPlaylist.toInt())
        }
    }

    @Query("""
        SELECT tracks.*
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        WHERE playlistId = :playlistId
        ORDER BY idInPlaylist
    """)
    abstract fun observePlaylistTracksImpl(playlistId: Long): Flowable<List<PodcastPlaylistTrackEntity>>

    fun observePlaylistTracks(playlistId: Long, podcastGateway: PodcastGateway): Flow<List<Song>> {
        return observePlaylistTracksImpl(playlistId)
            .asFlow()
            .map { trackList ->
                val songList : Map<Long, List<Song>> = podcastGateway.getAll().groupBy { it.id }
                trackList.mapNotNull { entity ->
                    songList[entity.podcastId]?.get(0)?.copy(idInPlaylist = entity.idInPlaylist.toInt())
                }
            }.assertBackground()
    }

    @Query("""
        SELECT max(idInPlaylist)
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        WHERE playlistId = :playlistId
    """)
    abstract suspend fun getPlaylistMaxId(playlistId: Long): Int?

    @Insert
    abstract suspend fun createPlaylist(playlist: PodcastPlaylistEntity): Long

    @Query("""
        UPDATE podcast_playlist SET name = :name WHERE id = :id
    """)
    abstract suspend fun renamePlaylist(id: Long, name: String)

    @Query("""DELETE FROM podcast_playlist WHERE id = :id""")
    abstract suspend fun deletePlaylist(id: Long)

    @Insert
    abstract suspend fun insertTracks(tracks: List<PodcastPlaylistTrackEntity>)

    @Query("""
        DELETE FROM podcast_playlist_tracks
        WHERE playlistId = :playlistId AND idInPlaylist = :idInPlaylist
    """)
    abstract suspend fun deleteTrack(playlistId: Long, idInPlaylist: Long)

    @Query("""
        DELETE FROM podcast_playlist_tracks WHERE playlistId = :id
    """)
    abstract suspend fun clearPlaylist(id: Long)

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
    abstract suspend fun removeDuplicated(id: Long)

    @Update
    abstract suspend fun updateTrackList(list: List<PodcastPlaylistTrackEntity>)

}