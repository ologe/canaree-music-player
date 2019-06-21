package dev.olog.msc.domain.gateway

import dev.olog.core.entity.PodcastPlaylist
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface PodcastPlaylistGateway :
        BaseGateway<PodcastPlaylist, Long>,
        ChildsHasPodcasts<Long>,
        HasMostPlayed {

    fun getAllAutoPlaylists() : Observable<List<PodcastPlaylist>>

    fun createPlaylist(playlistName: String): Single<Long>

    fun renamePlaylist(playlistId: Long, newTitle: String): Completable

    fun deletePlaylist(playlistId: Long): Completable

    fun clearPlaylist(playlistId: Long): Completable

    fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>): Completable

    fun getPlaylistsBlocking(): List<PodcastPlaylist>

    fun removeSongFromPlaylist(playlistId: Long, idInPlaylist: Long): Completable

    fun removeDuplicated(playlistId: Long): Completable

    fun moveItem(playlistId: Long, from: Int, to: Int): Boolean

    fun insertPodcastToHistory(podcastId: Long): Completable

}