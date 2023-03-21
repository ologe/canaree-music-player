package dev.olog.core.gateway.track

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface GenreGateway {

    fun getAll(): List<Genre>
    fun observeAll(): Flow<List<Genre>>

    fun getById(id: Long): Genre?
    fun observeById(id: Long): Flow<Genre?>

    fun getTrackListById(id: Long): List<Song>
    fun observeTrackListById(id: Long): Flow<List<Song>>

    fun observeMostPlayed(mediaId: MediaId): Flow<List<Song>>
    suspend fun insertMostPlayed(mediaId: MediaId)

    fun observeSiblings(id: Long): Flow<List<Genre>>

    fun observeRelatedArtists(id: Long): Flow<List<Artist>>

    fun observeRecentlyAddedSongs(id: Long): Flow<List<Song>>

}