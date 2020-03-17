package dev.olog.core.gateway

import dev.olog.core.entity.favorite.FavoriteState
import dev.olog.core.entity.favorite.FavoriteItemState
import dev.olog.core.entity.favorite.FavoriteTrackType
import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface FavoriteGateway {

    fun getTracks(): List<Song>
    fun getPodcasts(): List<Song>

    fun observeTracks(): Flow<List<Song>>
    fun observePodcasts(): Flow<List<Song>>

    suspend fun addSingle(type: FavoriteTrackType, songId: Long)
    suspend fun addGroup(type: FavoriteTrackType, songListId: List<Long>)

    suspend fun deleteSingle(type: FavoriteTrackType, songId: Long)
    suspend fun deleteGroup(type: FavoriteTrackType, songListId: List<Long>)

    suspend fun deleteAll(type: FavoriteTrackType)

    suspend fun isFavorite(songId: Long, type: FavoriteTrackType): Boolean

    fun observeToggleFavorite(): Flow<FavoriteState>
    suspend fun updateFavoriteState(state: FavoriteItemState)

    suspend fun toggleFavorite()

}