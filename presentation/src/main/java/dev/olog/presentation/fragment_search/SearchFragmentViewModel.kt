package dev.olog.presentation.fragment_search

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import dev.olog.domain.interactor.search.*
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable

class SearchFragmentViewModel(
        application: Application,
        private val queryText: MutableLiveData<String>,
        val searchData: LiveData<Pair<MutableMap<SearchType, MutableList<DisplayableItem>>, String>>,
        private val searchHeaders: SearchHeaders,
        private val insertSearchSongUseCase: InsertRecentSearchSongUseCase,
        private val insertSearchAlbumUseCase: InsertRecentSearchAlbumUseCase,
        private val insertSearchArtistUseCase: InsertRecentSearchArtistUseCase,
        private val deleteRecentSearchSongUseCase: DeleteRecentSearchSongUseCase,
        private val deleteRecentSearchAlbumUseCase: DeleteRecentSearchAlbumUseCase,
        private val deleteRecentSearchArtistUseCase: DeleteRecentSearchArtistUseCase,
        private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase

) : AndroidViewModel(application) {

    fun setNewQuery(newQuery: String){
        queryText.value = newQuery
    }

    fun adjustDataMap(data: MutableMap<SearchType, MutableList<DisplayableItem>>) {
        adjustAlbums(data[SearchType.ALBUMS]!!)
        adjustArtists(data[SearchType.ARTISTS]!!)
        adjustSongs(data[SearchType.SONGS]!!)
    }

    private fun adjustAlbums(list: MutableList<DisplayableItem>){
        if (list.isNotEmpty()){
            val albumsHeaders = searchHeaders.albumsHeaders(list.size)
            if (list[0].mediaId == albumsHeaders[0].mediaId){
                albumsHeaders.forEachIndexed { index, displayableItem ->
                    list[index] = displayableItem
                }
            } else {
                list.clear()
                list.addAll(0, albumsHeaders)
            }
        }
    }

    private fun adjustArtists(list: MutableList<DisplayableItem>){
        if (list.isNotEmpty()){
            val artistsHeaders = searchHeaders.artistsHeaders(list.size)
            if (list[0].mediaId == artistsHeaders[0].mediaId){
                artistsHeaders.forEachIndexed { index, displayableItem ->
                    list[index] = displayableItem
                }
            } else {
                list.clear()
                list.addAll(0, artistsHeaders)
            }
        }
    }

    private fun adjustSongs(list: MutableList<DisplayableItem>){
        if (list.isNotEmpty()){
            val songHeaders = searchHeaders.songsHeaders(list.size)
            if (list[0].mediaId == songHeaders[0].mediaId){
                songHeaders.forEachIndexed { index, displayableItem ->
                    list[index] = displayableItem
                }
            } else {
                list.addAll(0, songHeaders)
            }
        }
    }

    fun insertSongToRecent(mediaId: String): Completable {
        val songId = MediaIdHelper.extractLeaf(mediaId).toLong()
        return insertSearchSongUseCase.execute(songId)
    }

    fun insertAlbumToRecent(mediaId: String): Completable {
        val albumId = MediaIdHelper.extractCategoryValue(mediaId).toLong()
        return insertSearchAlbumUseCase.execute(albumId)
    }

    fun insertArtistToRecent(mediaId: String): Completable {
        val artistId = MediaIdHelper.extractCategoryValue(mediaId).toLong()
        return insertSearchArtistUseCase.execute(artistId)
    }

    fun deleteFromRecent(mediaId: String): Completable{
        val category = MediaIdHelper.extractCategory(mediaId)
        return when (category) {
            MediaIdHelper.MEDIA_ID_BY_ALBUM -> {
                val albumId = MediaIdHelper.extractCategoryValue(mediaId).toLong()
                deleteRecentSearchAlbumUseCase.execute(albumId)
            }
            MediaIdHelper.MEDIA_ID_BY_ARTIST -> {
                val artistId = MediaIdHelper.extractCategoryValue(mediaId).toLong()
                deleteRecentSearchArtistUseCase.execute(artistId)
            }
            MediaIdHelper.MEDIA_ID_BY_ALL -> {
                val songId = MediaIdHelper.extractLeaf(mediaId).toLong()
                return deleteRecentSearchSongUseCase.execute(songId)
            }
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }

    fun clearRecentSearches(): Completable {
        return clearRecentSearchesUseCase.execute()
    }

}