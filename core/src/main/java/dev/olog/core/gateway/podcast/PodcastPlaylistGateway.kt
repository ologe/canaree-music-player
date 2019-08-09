package dev.olog.core.gateway.podcast

import dev.olog.core.entity.track.Playlist
import dev.olog.core.gateway.base.*

interface PodcastPlaylistGateway :
    BaseGateway<Playlist, Id>,
    ChildHasTracks<Id>,
    HasSiblings<Playlist, Id>,
    HasRelatedArtists<Id> {

    fun getAllAutoPlaylists(): List<Playlist>

    suspend fun createPlaylist(playlistName: String): Long

    suspend fun renamePlaylist(playlistId: Id, newTitle: String)

    suspend fun deletePlaylist(playlistId: Id)

    suspend fun clearPlaylist(playlistId: Id)

    suspend fun addSongsToPlaylist(playlistId: Id, songIds: List<Long>)

    suspend fun removeFromPlaylist(playlistId: Id, idInPlaylist: Long)

    suspend fun removeDuplicated(playlistId: Id)

    suspend fun insertPodcastToHistory(podcastId: Id)

    suspend fun moveItem(playlistId: Long, moveList: List<Pair<Int, Int>>)

}