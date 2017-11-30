package dev.olog.domain.gateway

import dev.olog.domain.entity.Playlist
import io.reactivex.Completable
import io.reactivex.Flowable

interface PlaylistGateway :
        BaseGateway<Playlist, Long>,
        ChildsHasSongs<Long>,
        HasMostPlayed<String> {

    fun deletePlaylist(id: Long): Completable

    fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>): Completable

    fun getActualPlaylists(): Flowable<List<Playlist>>

}