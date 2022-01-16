package dev.olog.core.favorite

import dev.olog.core.MediaStoreType
import dev.olog.core.MediaUri
import dev.olog.core.track.Song
import kotlinx.coroutines.flow.Flow

interface FavoriteGateway {

    fun getSongs(): List<Song>
    fun getPodcastEpisodes(): List<Song>

    fun observeSongs(): Flow<List<Song>>
    fun observePodcastEpisodes(): Flow<List<Song>>

    suspend fun add(uris: List<MediaUri>)

    suspend fun delete(uris: List<MediaUri>)
    suspend fun deleteAll(type: MediaStoreType)

    suspend fun isFavorite(uri: MediaUri): Boolean

    fun observeToggleFavorite(): Flow<FavoriteEnum>

    suspend fun toggleFavorite()

}