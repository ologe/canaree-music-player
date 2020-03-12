package dev.olog.core.gateway

import dev.olog.core.MediaId
import dev.olog.core.entity.SearchResult
import kotlinx.coroutines.flow.Flow

interface RecentSearchesGateway {

    fun getAll() : Flow<List<SearchResult>>

    suspend fun insertTrack(mediaId: MediaId.Track)
    suspend fun insertAlbum(albumId: Long)
    suspend fun insertArtist(artistId: Long)
    suspend fun insertPlaylist(playlistId: Long)
    suspend fun insertGenre(genreId: Long)
    suspend fun insertFolder(folderId: Long)

    suspend fun insertPodcastPlaylist(playlistid: Long)
    suspend fun insertPodcastArtist(artistId: Long)

    suspend fun deleteTrack(mediaId: MediaId.Track)
    suspend fun deleteAlbum(itemId: Long)
    suspend fun deleteArtist(itemId: Long)
    suspend fun deletePlaylist(itemId: Long)
    suspend fun deleteFolder(itemId: Long)
    suspend fun deleteGenre(itemId: Long)

    suspend fun deletePodcastPlaylist(playlistId: Long)
    suspend fun deletePodcastArtist(artistId: Long)

    suspend fun deleteAll()

}