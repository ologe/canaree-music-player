package dev.olog.core.gateway.track

import dev.olog.core.entity.sort.AllGenresSort
import dev.olog.core.entity.sort.GenreSongsSort
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface GenreGateway {

    fun getAll(): List<Genre>
    fun observeAll(): Flow<List<Genre>>

    fun getByParam(id: Long): Genre?
    fun observeByParam(id: Long): Flow<Genre?>

    fun getTrackListByParam(id: Long): List<Song>
    fun observeTrackListByParam(id: Long): Flow<List<Song>>

    fun observeMostPlayed(id: Long): Flow<List<Song>>
    suspend fun insertMostPlayed(genreId: Long, songId: Long)

    fun observeSiblings(id: Long): Flow<List<Genre>>

    fun observeRelatedArtists(id: Long): Flow<List<Artist>>

    fun observeRecentlyAdded(id: Long): Flow<List<Song>>

    fun setSort(sort: AllGenresSort)
    fun getSort(): AllGenresSort

    fun setSongSort(sort: GenreSongsSort)
    fun getSongSort(): GenreSongsSort

}