package dev.olog.core.gateway.track

import dev.olog.core.entity.sort.AllArtistsSort
import dev.olog.core.entity.sort.ArtistSongsSort
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface ArtistGateway {

    fun getAll(): List<Artist>
    fun observeAll(): Flow<List<Artist>>

    fun getByParam(id: Long): Artist?
    fun observeByParam(id: Long): Flow<Artist?>

    fun getTrackListByParam(id: Long): List<Song>
    fun observeTrackListByParam(id: Long): Flow<List<Song>>

    fun observeRecentlyAdded(): Flow<List<Artist>>

    fun observeLastPlayed(): Flow<List<Artist>>
    suspend fun addLastPlayed(id: Long)

    fun setSort(sort: AllArtistsSort)
    fun getSort(): AllArtistsSort

    fun setSongSort(sort: ArtistSongsSort)
    fun getSongSort(): ArtistSongsSort

}