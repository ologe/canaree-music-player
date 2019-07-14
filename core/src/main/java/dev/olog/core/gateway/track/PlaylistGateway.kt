package dev.olog.core.gateway.track

import dev.olog.core.entity.track.Playlist
import dev.olog.core.gateway.base.*
import io.reactivex.Completable
import io.reactivex.Single

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
    fun createPlaylist(playlistName: String): Single<Long>

    fun renamePlaylist(playlistId: Long, newTitle: String): Completable

    fun deletePlaylist(playlistId: Long): Completable

    fun clearPlaylist(playlistId: Long): Completable

    fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>): Completable

    fun insertSongToHistory(songId: Long): Completable

    fun moveItem(playlistId: Long, from: Int, to: Int): Boolean

    suspend fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long)

    fun removeDuplicated(playlistId: Long): Completable
}