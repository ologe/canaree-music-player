package dev.olog.core.gateway.track

import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface ArtistGateway {

    fun getAll(): List<Artist>
    fun observeAll(): Flow<List<Artist>>

    fun getById(id: Long): Artist?
    fun observeById(id: Long): Flow<Artist?>

    fun getTrackListById(id: Long): List<Song>
    fun observeTrackListById(id: Long): Flow<List<Song>>

    fun observeRecentlyAdded(): Flow<List<Artist>>

    fun observeRecentlyPlayed(): Flow<List<Artist>>
    suspend fun addRecentlyPlayed(id: Long)

}