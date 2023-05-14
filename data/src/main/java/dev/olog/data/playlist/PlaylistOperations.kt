package dev.olog.data.playlist

import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.data.mediastore.MediaStorePlaylistInternalEntity
import dev.olog.data.mediastore.MediaStoreQuery

interface PlaylistOperations {

    suspend fun createPlaylist(title: String): MediaStorePlaylistInternalEntity?
    suspend fun renamePlaylist(playlist: Playlist, title: String): Boolean
    suspend fun deletePlaylist(playlist: Playlist): Boolean
    suspend fun clearPlaylist(playlist: Playlist): Boolean

    suspend fun addSongsToPlaylist(playlist: Playlist, songs: List<Song>): List<MediaStoreQuery.PlaylistTrack>
    suspend fun removeFromPlaylist(playlist: Playlist, idInPlaylist: Long): Boolean
    suspend fun overridePlaylistMembers(playlist: Playlist, songs: List<Song>): List<MediaStoreQuery.PlaylistTrack>

    suspend fun moveItem(playlist: Playlist, moveList: List<Pair<Int, Int>>): Int

}