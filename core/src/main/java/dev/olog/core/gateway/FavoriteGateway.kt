package dev.olog.core.gateway

import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface FavoriteGateway {

    fun getTracks(): List<Song>
    fun getTracksCount(): Int
    fun getPodcasts(): List<Song>
    fun getPodcastsCount(): Int

    fun observeTracks(): Flow<List<Song>>
    fun observeTracksCount(): Flow<Int>
    fun observePodcasts(): Flow<List<Song>>
    fun observePodcastsCount(): Flow<Int>

    suspend fun addSingle(id: Long)
    suspend fun addGroup(ids: List<Long>)

    suspend fun deleteSingle(id: Long)
    suspend fun deleteGroup(ids: List<Long>)

    suspend fun deleteAll(isPodcast: Boolean)

    suspend fun isFavorite(id: Long): Boolean

    fun observePlayingFavorite(): Flow<Boolean>
    suspend fun updateFavoriteState(id: String, isFavourite: Boolean)

    suspend fun toggleFavorite()

}