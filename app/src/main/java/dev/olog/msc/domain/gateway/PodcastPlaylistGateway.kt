package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.PodcastPlaylist
import io.reactivex.Completable
import io.reactivex.Single

interface PodcastPlaylistGateway :
        BaseGateway<PodcastPlaylist, Long>,
        ChildsHasSongs<Long>,
        HasMostPlayed {

    fun createPlaylist(playlistName: String): Single<Long>

    fun renamePlaylist(playlistId: Long, newTitle: String): Completable

    fun deletePlaylist(playlistId: Long): Completable

    fun clearPlaylist(playlistId: Long): Completable

    fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>): Completable

    fun getPlaylistsBlocking(): List<PodcastPlaylist>

    fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long): Completable

    fun removeDuplicated(playlistId: Long): Completable

}