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
            list.clear()
            list.addAll(searchHeaders.albumsHeaders(list.size))
        }
    }

    private fun adjustArtists(list: MutableList<DisplayableItem>){
        if (list.isNotEmpty()){
            list.clear()
            list.addAll(searchHeaders.artistsHeaders(list.size))
        }
    }

    private fun adjustSongs(list: MutableList<DisplayableItem>){
        if (list.isNotEmpty()){
            list.add(0, searchHeaders.songsHeaders(list.size))
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
            MediaIdCategory.ALBUMS -> {
                val albumId = mediaId.categoryValue.toLong()
                deleteRecentSearchAlbumUseCase.execute(albumId)
            }
            MediaIdCategory.ARTISTS -> {
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