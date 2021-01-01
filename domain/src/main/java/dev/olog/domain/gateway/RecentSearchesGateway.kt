package dev.olog.domain.gateway

import dev.olog.domain.entity.SearchResult
import kotlinx.coroutines.flow.Flow

interface RecentSearchesGateway {

    fun getAll() : Flow<List<SearchResult>>

    suspend fun insertSong(songId: Long)
    suspend fun insertAlbum(albumId: Long)
    suspend fun insertArtist(artistId: Long)
    suspend fun insertPlaylist(playlistId: Long)
    suspend fun insertGenre(genreId: Long)
    suspend fun insertFolder(folderId: Long)

    suspend fun insertPodcast(podcastId: Long)
    suspend fun insertPodcastPlaylist(playlistid: Long)
    suspend fun insertPodcastAlbum(albumId: Long)
    suspend fun insertPodcastArtist(artistId: Long)

    suspend fun deleteSong(itemId: Long)
    suspend fun deleteAlbum(itemId: Long)
    suspend fun deleteArtist(itemId: Long)
    suspend fun deletePlaylist(itemId: Long)
    suspend fun deleteFolder(itemId: Long)
    suspend fun deleteGenre(itemId: Long)

    suspend fun deletePodcast(podcastId: Long)
    suspend fun deletePodcastPlaylist(playlistId: Long)
    suspend fun deletePodcastAlbum(albumId: Long)
    suspend fun deletePodcastArtist(artistId: Long)

    suspend fun deleteAll()

}