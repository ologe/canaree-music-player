package dev.olog.core.folder

import dev.olog.core.author.Artist
import dev.olog.core.entity.MostPlayedSong
import dev.olog.core.MediaUri
import dev.olog.core.track.Song
import dev.olog.core.sort.FolderDetailSort
import dev.olog.core.sort.GenericSort
import dev.olog.core.sort.Sort
import kotlinx.coroutines.flow.Flow

interface FolderGateway {

    fun getAll(): List<Folder>
    fun observeAll(): Flow<List<Folder>>

    fun getById(uri: MediaUri): Folder?
    fun observeById(uri: MediaUri): Flow<Folder?>

    fun getTracksById(uri: MediaUri): List<Song>
    fun observeTracksById(uri: MediaUri): Flow<List<Song>>

    fun getAllBlacklistedIncluded(): List<Folder>

    fun observeMostPlayed(uri: MediaUri): Flow<List<MostPlayedSong>>
    suspend fun insertMostPlayed(uri: MediaUri, trackUri: MediaUri)

    fun observeSiblingsById(uri: MediaUri): Flow<List<Folder>>

    fun observeRelatedArtistsById(uri: MediaUri): Flow<List<Artist>>

    fun observeRecentlyAddedTracksById(uri: MediaUri): Flow<List<Song>>

    fun getSort(): Sort<GenericSort>
    fun setSort(sort: Sort<GenericSort>)
    fun getDetailSort(): Sort<FolderDetailSort>
    fun observeDetailSort(): Flow<Sort<FolderDetailSort>>
    fun setDetailSort(sort: Sort<FolderDetailSort>)

}