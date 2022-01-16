package dev.olog.core.playlist

import dev.olog.core.MediaStoreType
import dev.olog.core.author.Artist
import dev.olog.core.entity.MostPlayedSong
import dev.olog.core.MediaUri
import dev.olog.core.track.Song
import dev.olog.core.sort.GenericSort
import dev.olog.core.sort.PlaylistDetailSort
import dev.olog.core.sort.Sort
import kotlinx.coroutines.flow.Flow

interface PlaylistGateway {

    fun observeAutoPlaylist(type: MediaStoreType): Flow<List<Playlist>>

    fun getAll(type: MediaStoreType): List<Playlist>
    fun observeAll(type: MediaStoreType): Flow<List<Playlist>>

    fun getById(uri: MediaUri): Playlist?
    fun observeById(uri: MediaUri): Flow<Playlist?>

    fun getTracksById(uri: MediaUri): List<Song>
    fun observeTracksById(uri: MediaUri): Flow<List<Song>>

    fun observeMostPlayed(uri: MediaUri): Flow<List<MostPlayedSong>>
    suspend fun insertMostPlayed(uri: MediaUri, trackUri: MediaUri)

    fun observeSiblingsById(uri: MediaUri): Flow<List<Playlist>>

    fun observeRelatedArtistsById(uri: MediaUri): Flow<List<Artist>>

    fun getSort(type: MediaStoreType): Sort<GenericSort>
    fun setSort(type: MediaStoreType, sort: Sort<GenericSort>)
    fun getDetailSort(type: MediaStoreType): Sort<PlaylistDetailSort>
    fun observeDetailSort(type: MediaStoreType): Flow<Sort<PlaylistDetailSort>>
    fun setDetailSort(type: MediaStoreType, sort: Sort<PlaylistDetailSort>)

    suspend fun createPlaylist(title: String, tracksUri: List<MediaUri>)

    suspend fun createPlaylist(title: String, trackUri: MediaUri) {
        return createPlaylist(title, listOf(trackUri))
    }

    suspend fun rename(uri: MediaUri, title: String)

    suspend fun delete(uri: MediaUri)

    suspend fun clear(uri: MediaUri)

    suspend fun addTracks(uri: MediaUri, tracksUri: List<MediaUri>)

    suspend fun moveItem(uri: MediaUri, moveList: List<Pair<Int, Int>>)

    suspend fun remove(uri: MediaUri, position: Int)

    suspend fun removeDuplicated(uri: MediaUri)

}