package dev.olog.msc.presentation.search

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import dev.olog.msc.domain.interactor.search.*
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import io.reactivex.Completable

class SearchFragmentViewModel(
        application: Application,
        private val queryText: MutableLiveData<String>,
        val searchData: LiveData<Pair<MutableMap<SearchFragmentType, MutableList<DisplayableItem>>, String>>,
        private val searchHeaders: SearchFragmentHeaders,
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

    fun adjustDataMap(data: MutableMap<SearchFragmentType, MutableList<DisplayableItem>>) {
        adjustAlbums(data[SearchFragmentType.ALBUMS]!!)
        adjustArtists(data[SearchFragmentType.ARTISTS]!!)
        adjustSongs(data[SearchFragmentType.SONGS]!!)
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

    fun insertSongToRecent(mediaId: MediaId): Completable {
        return insertSearchSongUseCase.execute(mediaId.leaf!!)
    }

    fun insertAlbumToRecent(mediaId: MediaId): Completable {
        val albumId = mediaId.categoryValue.toLong()
        return insertSearchAlbumUseCase.execute(albumId)
    }

    fun insertArtistToRecent(mediaId: MediaId): Completable {
        val artistId = mediaId.categoryValue.toLong()
        return insertSearchArtistUseCase.execute(artistId)
    }

    fun deleteFromRecent(mediaId: MediaId): Completable{
        return when (mediaId.category) {
            MediaIdCategory.ALBUM -> {
                val albumId = mediaId.categoryValue.toLong()
                deleteRecentSearchAlbumUseCase.execute(albumId)
            }
            MediaIdCategory.ARTIST -> {
                val artistId = mediaId.categoryValue.toLong()
                deleteRecentSearchArtistUseCase.execute(artistId)
            }
            MediaIdCategory.SONGS -> {
                return deleteRecentSearchSongUseCase.execute(mediaId.leaf!!)
            }
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }

    fun clearRecentSearches(): Completable {
        return clearRecentSearchesUseCase.execute()
    }

}