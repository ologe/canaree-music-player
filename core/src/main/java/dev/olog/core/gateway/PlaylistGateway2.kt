package dev.olog.core.gateway

import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import io.reactivex.Completable
import io.reactivex.Single

interface PlaylistGateway2 :
        BaseGateway2<Playlist, Id>,
        ChildHasTracks2<Song, Id>,
        HasMostPlayed2,
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

    fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long): Completable

    fun removeDuplicated(playlistId: Long): Completable
}