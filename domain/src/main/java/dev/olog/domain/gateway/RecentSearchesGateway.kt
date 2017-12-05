package dev.olog.domain.gateway

import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import dev.olog.domain.entity.SearchResult
import dev.olog.domain.entity.Song
import io.reactivex.Completable
import io.reactivex.Flowable

interface RecentSearchesGateway {

    fun getAll() : Flowable<List<SearchResult>>

    fun insertSong(song: Song): Completable
    fun insertAlbum(album: Album): Completable
    fun insertArtist(artist: Artist): Completable

    fun deleteItem(dataType: Int, itemId: Long): Completable
    fun deleteAll(): Completable

}