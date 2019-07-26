package dev.olog.core.gateway

import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.core.entity.favorite.FavoriteStateEntity
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.entity.track.Song
import io.reactivex.Completable
import kotlinx.coroutines.flow.Flow

interface FavoriteGateway {

    fun getTracks(): List<Song>
    fun getPodcasts(): List<Song>

    fun observeTracks(): Flow<List<Song>>
    fun observePodcasts(): Flow<List<Song>>

    fun addSingle(type: FavoriteType, songId: Long): Completable
    fun addGroup(type: FavoriteType, songListId: List<Long>): Completable

    suspend fun deleteSingle(type: FavoriteType, songId: Long)
    suspend fun deleteGroup(type: FavoriteType, songListId: List<Long>)

    fun deleteAll(type: FavoriteType): Completable

    suspend fun isFavorite(type: FavoriteType, songId: Long): Boolean

    fun observeToggleFavorite(): Flow<FavoriteEnum>
    fun updateFavoriteState(state: FavoriteStateEntity)

    suspend fun toggleFavorite()

}