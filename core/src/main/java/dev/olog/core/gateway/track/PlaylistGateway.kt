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
    fun createPlaylist(playlistName: String): Long

    fun renamePlaylist(playlistId: Long, newTitle: String): Completable

    fun deletePlaylist(playlistId: Long): Completable

    fun clearPlaylist(playlistId: Long): Completable

    suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>)

    fun insertSongToHistory(songId: Long): Completable

    fun moveItem(playlistId: Long, from: Int, to: Int): Boolean

    suspend fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long)

    suspend fun removeDuplicated(playlistId: Long)
}