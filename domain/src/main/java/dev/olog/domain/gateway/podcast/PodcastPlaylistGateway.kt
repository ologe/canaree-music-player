package dev.olog.domain.gateway.podcast

import dev.olog.domain.entity.track.Playlist
import dev.olog.domain.gateway.base.BaseGateway
import dev.olog.domain.gateway.base.ChildHasTracks
import dev.olog.domain.gateway.base.HasRelatedArtists
import dev.olog.domain.gateway.base.HasSiblings

interface PodcastPlaylistGateway :
    BaseGateway<Playlist, Long>,
    ChildHasTracks<Long>,
    HasSiblings<Playlist, Long>,
    HasRelatedArtists<Long> {

    fun getAllAutoPlaylists(): List<Playlist>

    suspend fun createPlaylist(playlistName: String): Long

    suspend fun renamePlaylist(playlistId: Long, newTitle: String)

    suspend fun deletePlaylist(playlistId: Long)

    suspend fun clearPlaylist(playlistId: Long)

    suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>)

    suspend fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long)

    suspend fun removeDuplicated(playlistId: Long)

    suspend fun insertPodcastToHistory(podcastId: Long)

    suspend fun moveItem(playlistId: Long, moveList: List<Pair<Int, Int>>)

}