package dev.olog.core.gateway.track

import dev.olog.core.MediaId
import dev.olog.core.entity.track.AutoPlaylist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.QueryMode
import kotlinx.coroutines.flow.Flow

interface AutoPlaylistGateway {

    fun observeAll(mode: QueryMode): Flow<List<AutoPlaylist>>
    fun getById(id: Long): AutoPlaylist?
    fun observeById(id: Long): Flow<AutoPlaylist?>

    fun getTrackListById(id: Long): List<Song>
    fun observeTrackListById(id: Long): Flow<List<Song>>

    suspend fun clearPlaylist(id: Long)
    suspend fun removeFromAutoPlaylist(mediaId: MediaId, trackId: Long)

    suspend fun insertToHistory(mediaId: MediaId)

}