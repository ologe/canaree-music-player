package dev.olog.msc.presentation.search

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import dev.olog.msc.domain.interactor.search.*
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import io.reactivex.Completable

class SearchFragmentViewModel(
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

) : ViewModel() {

    fun setNewQuery(newQuery: String){
        queryText.value = newQuery
    }

    fun adjustDataMap(data: MutableMap<SearchFragmentType, MutableList<DisplayableItem>>)
            : MutableMap<SearchFragmentType, MutableList<DisplayableItem>>{

        val map = data.toMutableMap()
        map[SearchFragmentType.ALBUMS] = adjustAlbums(data[SearchFragmentType.ALBUMS]!!)
        map[SearchFragmentType.ARTISTS] = adjustArtists(data[SearchFragmentType.ARTISTS]!!)
        map[SearchFragmentType.SONGS] = adjustSongs(data[SearchFragmentType.SONGS]!!)
        return map
    }

    private fun adjustAlbums(list: MutableList<DisplayableItem>): MutableList<DisplayableItem> {
        return if (list.isNotEmpty()){
            val size = list.size
            return searchHeaders.albumsHeaders(size)
        } else mutableListOf()
    }

    private fun adjustArtists(list: MutableList<DisplayableItem>): MutableList<DisplayableItem>{
        return if (list.isNotEmpty()){
            val size = list.size
            searchHeaders.artistsHeaders(size)
        } else mutableListOf()
    }

    private fun adjustSongs(list: MutableList<DisplayableItem>): MutableList<DisplayableItem>{
        return if (list.isNotEmpty()){
            val copy = list.toMutableList()
            copy.add(0, searchHeaders.songsHeaders(list.size))
            copy
        } else mutableListOf()
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