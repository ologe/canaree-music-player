package dev.olog.domain.gateway

import dev.olog.domain.entity.Playlist
import io.reactivex.Completable
import io.reactivex.Single

interface PlaylistGateway :
        BaseGateway<Playlist, Long>,
        ChildsHasSongs<Long>,
        HasMostPlayed<String> {

    fun createPlaylist(playlistName: String): Single<Long>

    fun deletePlaylist(id: Long): Completable

    fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>): Single<String>

    fun getActualPlaylistsBlocking(): List<Playlist>

}