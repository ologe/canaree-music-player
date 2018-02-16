package dev.olog.msc.presentation.search

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import dev.olog.msc.domain.interactor.search.*
import dev.olog.msc.domain.interactor.tab.GetAllAlbumsUseCase
import dev.olog.msc.domain.interactor.tab.GetAllArtistsUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Singles
import me.xdrop.fuzzywuzzy.FuzzySearch
import me.xdrop.fuzzywuzzy.model.ExtractedResult

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
        private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase,
        private val getAllArtistsUseCase: GetAllArtistsUseCase,
        private val getAllAlbumsUseCase: GetAllAlbumsUseCase

) : ViewModel() {

    fun setNewQuery(newQuery: String){
        queryText.value = newQuery.trim()
    }

    fun getBestMatch(query: String): Single<String> {
        return Singles.zip(
                getAllArtistsUseCase.execute().firstOrError(),
                getAllAlbumsUseCase.execute().firstOrError(),
                { artists, albums -> listOf(
                        artists.map { it.name },
                        albums.map { it.title }
                ) })
                .flattenAsFlowable { it }
                .parallel()
                .map { extractBest(query, it) }
                .sequential()
                .toList()
                .map { it.filter { it.score >= 75 }.maxBy { it.score }?.string ?: ""}
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun extractBest(query: String, list: List<String>): ExtractedResult {
        return FuzzySearch.extractOne(query, list)
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