package dev.olog.core.gateway

import dev.olog.core.MediaId
import dev.olog.core.entity.SearchResult
import kotlinx.coroutines.flow.Flow

interface RecentSearchesGateway {

    fun getAll() : Flow<List<SearchResult>>

    suspend fun insertTrack(mediaId: MediaId.Track)
    suspend fun insertAlbum(albumId: String)
    suspend fun insertArtist(artistId: String)
    suspend fun insertPlaylist(playlistId: String)
    suspend fun insertGenre(genreId: String)
    suspend fun insertFolder(folderId: String)

    suspend fun insertPodcastPlaylist(playlistid: String)
    suspend fun insertPodcastArtist(artistId: String)

    suspend fun deleteTrack(mediaId: MediaId.Track)
    suspend fun deleteAlbum(itemId: String)
    suspend fun deleteArtist(itemId: String)
    suspend fun deletePlaylist(itemId: String)
    suspend fun deleteFolder(itemId: String)
    suspend fun deleteGenre(itemId: String)

    suspend fun deletePodcastPlaylist(playlistId: String)
    suspend fun deletePodcastArtist(artistId: String)

    suspend fun deleteAll()

}