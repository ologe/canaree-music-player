package dev.olog.core.gateway.track

import dev.olog.core.entity.track.Playlist
import dev.olog.core.gateway.base.*
import io.reactivex.Completable

interface PlaylistGateway :
    BaseGateway<Playlist, Id>,
    ChildHasTracks<Id>,
    HasMostPlayed,
    HasSiblings<Playlist, Id>,
    PlaylistOperations,
    HasRelatedArtists<Id> {

    fun getAllAutoPlaylists(): List<Playlist>

}

interface PlaylistOperations {
    suspend fun createPlaylist(playlistName: String): Long

    suspend fun renamePlaylist(playlistId: Long, newTitle: String)

    suspend fun deletePlaylist(playlistId: Long)

    suspend fun clearPlaylist(playlistId: Long)

    suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>)

    suspend fun insertSongToHistory(songId: Long)

    fun moveItem(playlistId: Long, from: Int, to: Int): Boolean

    suspend fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long)

    suspend fun removeDuplicated(playlistId: Long)
}