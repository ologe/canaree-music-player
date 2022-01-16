package dev.olog.core.genre

import dev.olog.core.author.Artist
import dev.olog.core.entity.MostPlayedSong
import dev.olog.core.MediaUri
import dev.olog.core.track.Song
import dev.olog.core.sort.GenericSort
import dev.olog.core.sort.GenreDetailSort
import dev.olog.core.sort.Sort
import kotlinx.coroutines.flow.Flow

interface GenreGateway {

    fun getAll(): List<Genre>
    fun observeAll(): Flow<List<Genre>>

    fun getById(uri: MediaUri): Genre?
    fun observeById(uri: MediaUri): Flow<Genre?>

    fun getTracksById(uri: MediaUri): List<Song>
    fun observeTracksById(uri: MediaUri): Flow<List<Song>>

    fun observeMostPlayed(uri: MediaUri): Flow<List<MostPlayedSong>>
    suspend fun insertMostPlayed(uri: MediaUri, trackUri: MediaUri)

    fun observeSiblingsById(uri: MediaUri): Flow<List<Genre>>

    fun observeRelatedArtistsById(uri: MediaUri): Flow<List<Artist>>

    fun observeRecentlyAddedTracksById(uri: MediaUri): Flow<List<Song>>

    fun getSort(): Sort<GenericSort>
    fun setSort(sort: Sort<GenericSort>)
    fun getDetailSort(): Sort<GenreDetailSort>
    fun observeDetailSort(): Flow<Sort<GenreDetailSort>>
    fun setDetailSort(sort: Sort<GenreDetailSort>)

}