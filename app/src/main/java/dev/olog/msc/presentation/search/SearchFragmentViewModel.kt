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
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.addTo
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

    private var isFirstAccess = true

    private val subscriptions = CompositeDisposable()

    fun doOnFirstAccess(func: () -> Unit){
        if (isFirstAccess){
            isFirstAccess = false
            func()
        }
    }

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

    fun insertToRecent(mediaId: MediaId){
        when {
            mediaId.isLeaf -> insertSearchSongUseCase.execute(mediaId.leaf!!)
            mediaId.isArtist -> insertSearchArtistUseCase.execute(mediaId.resolveId)
            mediaId.isAlbum -> insertSearchAlbumUseCase.execute(mediaId.resolveId)
            else -> throw IllegalArgumentException("invalid category ${mediaId.resolveId}")
        }.subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

//    fun insertSongToRecent(mediaId: MediaId) {
//        insertSearchSongUseCase.execute(mediaId.leaf!!)
//                .subscribe({}, Throwable::printStackTrace)
//                .addTo(subscriptions)
//    }

//    fun insertAlbumToRecent(mediaId: MediaId) {
//        val albumId = mediaId.resolveId
//        insertSearchAlbumUseCase.execute(albumId)
//                .subscribe({}, Throwable::printStackTrace)
//                .addTo(subscriptions)
//    }

//    fun insertArtistToRecent(mediaId: MediaId) {
//        val artistId = mediaId.resolveId
//        insertSearchArtistUseCase.execute(artistId)
//                .subscribe({}, Throwable::printStackTrace)
//                .addTo(subscriptions)
//    }

    fun deleteFromRecent(mediaId: MediaId){
        when (mediaId.category) {
            MediaIdCategory.ALBUMS -> deleteRecentSearchAlbumUseCase.execute(mediaId.resolveId)
            MediaIdCategory.ARTISTS -> deleteRecentSearchArtistUseCase.execute(mediaId.resolveId)
            MediaIdCategory.SONGS -> deleteRecentSearchSongUseCase.execute(mediaId.leaf!!)
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }.subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    fun clearRecentSearches() {
        clearRecentSearchesUseCase.execute()
                .subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    override fun onCleared() {
        subscriptions.clear()
    }

}