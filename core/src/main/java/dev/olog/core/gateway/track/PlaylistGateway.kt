package dev.olog.core.gateway.track

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.QueryMode
import kotlinx.coroutines.flow.Flow

interface PlaylistGateway {

    fun getAll(mode: QueryMode): List<Playlist>
    fun observeAll(mode: QueryMode): Flow<List<Playlist>>

    fun getById(id: Long): Playlist?
    fun observeById(id: Long): Flow<Playlist?>

    fun getTrackListById(mediaId: MediaId): List<Song>
    fun observeTrackListById(mediaId: MediaId): Flow<List<Song>>

    fun observeMostPlayed(mediaId: MediaId): Flow<List<Song>>
    suspend fun insertMostPlayed(parentMediaId: MediaId, mediaId: MediaId)

    fun observeSiblings(mediaId: MediaId): Flow<List<Playlist>>

    fun observeRelatedArtists(id: Long): Flow<List<Artist>>

    suspend fun createPlaylist(title: String): Long?
    suspend fun renamePlaylist(playlistId: Long, newTitle: String)
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun clearPlaylist(playlistId: Long)
    suspend fun addSongsToPlaylist(playlistId: Long, songs: List<Song>): Int
    suspend fun moveItem(playlistId: Long, moveList: List<Pair<Int, Int>>)
    suspend fun removeFromPlaylist(mediaId: MediaId, idInPlaylist: Long)
    suspend fun removeDuplicated(playlistId: Long)

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getPlaylistDirectory(): Uri?
    @RequiresApi(Build.VERSION_CODES.Q)
    fun setPlaylistDirectory(documentUri: Uri?)

}