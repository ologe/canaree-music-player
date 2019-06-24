package dev.olog.core.gateway

import dev.olog.core.entity.SearchResult
import io.reactivex.Completable
import io.reactivex.Observable

interface RecentSearchesGateway {

    fun getAll() : Observable<List<SearchResult>>

    fun insertSong(songId: Long): Completable
    fun insertAlbum(albumId: Long): Completable
    fun insertArtist(artistId: Long): Completable
    fun insertPlaylist(playlistId: Long): Completable
    fun insertGenre(genreId: Long): Completable
    fun insertFolder(folderId: Long): Completable

    fun insertPodcast(podcastId: Long): Completable
    fun insertPodcastPlaylist(playlistid: Long): Completable
    fun insertPodcastAlbum(albumId: Long): Completable
    fun insertPodcastArtist(artistId: Long): Completable

    fun deleteSong(itemId: Long): Completable
    fun deleteAlbum(itemId: Long): Completable
    fun deleteArtist(itemId: Long): Completable
    fun deletePlaylist(itemId: Long): Completable
    fun deleteFolder(itemId: Long): Completable
    fun deleteGenre(itemId: Long): Completable

    fun deletePodcast(podcastId: Long): Completable
    fun deletePodcastPlaylist(playlistId: Long): Completable
    fun deletePodcastAlbum(albumId: Long): Completable
    fun deletePodcastArtist(artistId: Long): Completable

    fun deleteAll(): Completable

}