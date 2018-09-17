package dev.olog.msc.presentation.search

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import dev.olog.msc.domain.interactor.all.*
import dev.olog.msc.domain.interactor.search.delete.*
import dev.olog.msc.domain.interactor.search.insert.*
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
import javax.inject.Inject

class SearchFragmentViewModel @Inject constructor(
        private val queryText: MutableLiveData<String>,
        val searchData: LiveData<Pair<MutableMap<SearchFragmentType, MutableList<DisplayableItem>>, String>>,
        private val searchHeaders: SearchFragmentHeaders,
        private val insertSearchSongUseCase: InsertRecentSearchSongUseCase,
        private val insertSearchAlbumUseCase: InsertRecentSearchAlbumUseCase,
        private val insertSearchArtistUseCase: InsertRecentSearchArtistUseCase,
        private val insertSearchPlaylistUseCase: InsertRecentSearchPlaylistUseCase,
        private val insertSearchGenreUseCase: InsertRecentSearchGenreUseCase,
        private val insertSearchFolderUseCase: InsertRecentSearchFolderUseCase,
        private val deleteRecentSearchSongUseCase: DeleteRecentSearchSongUseCase,
        private val deleteRecentSearchAlbumUseCase: DeleteRecentSearchAlbumUseCase,
        private val deleteRecentSearchArtistUseCase: DeleteRecentSearchArtistUseCase,
        private val deleteRecentSearchPlaylistUseCase: DeleteRecentSearchPlaylistUseCase,
        private val deleteRecentSearchGenreUseCase: DeleteRecentSearchGenreUseCase,
        private val deleteRecentSearchFolderUseCase: DeleteRecentSearchFolderUseCase,
        private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase,
        private val getAllArtistsUseCase: GetAllArtistsUseCase,
        private val getAllAlbumsUseCase: GetAllAlbumsUseCase

) : ViewModel() {

    private val subscriptions = CompositeDisposable()

    fun setNewQuery(newQuery: String){
        queryText.value = newQuery.trim()
    }

    fun getBestMatch(query: String): Single<String> {
        return Singles.zip(
                getAllArtistsUseCase.execute().firstOrError(),
                getAllAlbumsUseCase.execute().firstOrError()
        ) { artists, albums -> listOf(
                artists.map { it.name },
                albums.map { it.title }
        ) }
                .flattenAsFlowable { it }
                .parallel()
                .map { extractBest(query, it) }
                .sequential()
                .toList()
                .map { list -> list.filter { it.score >= 75 }.maxBy { it.score }?.string ?: ""}
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
        map[SearchFragmentType.PLAYLISTS] = adjustPlaylists(data[SearchFragmentType.PLAYLISTS]!!)
        map[SearchFragmentType.FOLDERS] = adjustFolders(data[SearchFragmentType.FOLDERS]!!)
        map[SearchFragmentType.GENRES] = adjustGenres(data[SearchFragmentType.GENRES]!!)
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

    private fun adjustFolders(list: MutableList<DisplayableItem>): MutableList<DisplayableItem>{
        return if (list.isNotEmpty()){
            val size = list.size
            searchHeaders.foldersHeaders(size)
        } else mutableListOf()
    }

    private fun adjustPlaylists(list: MutableList<DisplayableItem>): MutableList<DisplayableItem>{
        return if (list.isNotEmpty()){
            val size = list.size
            searchHeaders.playlistsHeaders(size)
        } else mutableListOf()
    }

    private fun adjustGenres(list: MutableList<DisplayableItem>): MutableList<DisplayableItem>{
        return if (list.isNotEmpty()){
            val size = list.size
            searchHeaders.genreHeaders(size)
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
            mediaId.isPlaylist -> insertSearchPlaylistUseCase.execute(mediaId.resolveId)
            mediaId.isFolder -> insertSearchFolderUseCase.execute(mediaId.resolveId)
            mediaId.isGenre -> insertSearchGenreUseCase.execute(mediaId.resolveId)
            else -> throw IllegalArgumentException("invalid category ${mediaId.resolveId}")
        }.subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    fun deleteFromRecent(mediaId: MediaId){
        when (mediaId.category) {
            MediaIdCategory.ALBUMS -> deleteRecentSearchAlbumUseCase.execute(mediaId.resolveId)
            MediaIdCategory.ARTISTS -> deleteRecentSearchArtistUseCase.execute(mediaId.resolveId)
            MediaIdCategory.PLAYLISTS -> deleteRecentSearchPlaylistUseCase.execute(mediaId.resolveId)
            MediaIdCategory.FOLDERS -> deleteRecentSearchFolderUseCase.execute(mediaId.resolveId)
            MediaIdCategory.GENRES -> deleteRecentSearchGenreUseCase.execute(mediaId.resolveId)
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