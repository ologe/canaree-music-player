package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.SearchResult
import io.reactivex.Completable
import io.reactivex.Flowable

interface RecentSearchesGateway {

    fun getAll() : Flowable<List<SearchResult>>

    fun insertSong(songId: Long): Completable
    fun insertAlbum(albumId: Long): Completable
    fun insertArtist(artistId: Long): Completable

    fun deleteSong(itemId: Long): Completable
    fun deleteAlbum(itemId: Long): Completable
    fun deleteArtist(itemId: Long): Completable
    fun deleteAll(): Completable

}