package dev.olog.domain.gateway

import dev.olog.domain.entity.favorite.FavoriteEnum
import dev.olog.domain.entity.favorite.FavoriteStateEntity
import dev.olog.domain.entity.favorite.FavoriteType
import dev.olog.domain.entity.track.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteGateway {

    suspend fun getTracks(): List<Track>
    suspend fun getPodcasts(): List<Track>

    fun observeTracks(): Flow<List<Track>>
    fun observePodcasts(): Flow<List<Track>>

    suspend fun addSingle(type: FavoriteType, songId: Long)
    suspend fun addGroup(type: FavoriteType, songListId: List<Long>)

    suspend fun deleteSingle(type: FavoriteType, songId: Long)
    suspend fun deleteGroup(type: FavoriteType, songListId: List<Long>)

    suspend fun deleteAll(type: FavoriteType)

    suspend fun isFavorite(type: FavoriteType, songId: Long): Boolean

    fun observeToggleFavorite(): Flow<FavoriteEnum>
    suspend fun updateFavoriteState(state: FavoriteStateEntity)

    suspend fun toggleFavorite()

}