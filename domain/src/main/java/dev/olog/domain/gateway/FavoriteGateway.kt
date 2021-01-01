package dev.olog.domain.gateway

import dev.olog.domain.entity.Favorite
import dev.olog.domain.entity.track.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteGateway {

    suspend fun getTracks(): List<Track>
    suspend fun getPodcasts(): List<Track>

    fun observeTracks(): Flow<List<Track>>
    fun observePodcasts(): Flow<List<Track>>

    suspend fun addSingle(type: Favorite.Type, trackId: Long)
    suspend fun addGroup(type: Favorite.Type, trackList: List<Long>)

    suspend fun deleteSingle(type: Favorite.Type, trackId: Long)
    suspend fun deleteGroup(type: Favorite.Type, trackList: List<Long>)

    suspend fun deleteAll(type: Favorite.Type)

    suspend fun isFavorite(type: Favorite.Type, trackId: Long): Boolean

    fun observePlayingTrackFavoriteState(): Flow<Favorite.State>
    suspend fun updatePlayingTrackFavorite(state: Favorite)

    suspend fun togglePlayingTrackFavoriteState()

}