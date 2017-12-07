package dev.olog.domain.gateway

import dev.olog.domain.entity.Playlist
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface PlaylistGateway :
        BaseGateway<Playlist, Long>,
        ChildsHasSongs<Long>,
        HasMostPlayed<String> {

    fun getAllAutoPlaylists() : Flowable<List<Playlist>>

    fun createPlaylist(playlistName: String): Single<Long>

    fun renamePlaylist(playlistId: Long, newTitle: String): Completable

    fun deletePlaylist(playlistId: Long): Completable

    fun clearPlaylist(playlistId: Long): Completable

    fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>): Single<String>

    fun getActualPlaylistsBlocking(): List<Playlist>

    fun insertSongToHistory(songId: Long): Completable

}