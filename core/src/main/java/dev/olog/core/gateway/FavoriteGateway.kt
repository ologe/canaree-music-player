package dev.olog.core.gateway

import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface FavoriteGateway {

    fun getSongs(): List<Song>
    fun getPodcastEpisodes(): List<Song>

    fun observeSongs(): Flow<List<Song>>
    fun observePodcastEpisodes(): Flow<List<Song>>

    suspend fun addSingle(playableId: Long)
    suspend fun addGroup(playableIds: List<Long>)

    suspend fun deleteSingle(playableId: Long)
    suspend fun deleteGroup(playableIds: List<Long>)

    suspend fun deleteAll(type: FavoriteType)

    suspend fun isFavorite(playableId: Long): Boolean

    fun observeToggleFavorite(): Flow<FavoriteEnum>

    suspend fun toggleFavorite()

}