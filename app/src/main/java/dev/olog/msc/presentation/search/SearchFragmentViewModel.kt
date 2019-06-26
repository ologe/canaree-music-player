package dev.olog.msc.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.MediaId
import dev.olog.msc.domain.interactor.search.delete.ClearRecentSearchesUseCase
import dev.olog.msc.domain.interactor.search.delete.DeleteRecentSearchUseCase
import dev.olog.msc.domain.interactor.search.insert.InsertRecentSearchUseCase
import dev.olog.presentation.model.DisplayableItem
import io.reactivex.rxkotlin.addTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchFragmentViewModel @Inject constructor(
    private val searchDataProvider: SearchDataProvider,
    private val insertRecentUse: InsertRecentSearchUseCase,
    private val deleteRecentSearchUseCase: DeleteRecentSearchUseCase,
    private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase

) : ViewModel() {

    private val data = MutableLiveData<List<DisplayableItem>>()
    private val albumData = MutableLiveData<List<DisplayableItem>>()
    private val artistsData = MutableLiveData<List<DisplayableItem>>()
    private val genresData = MutableLiveData<List<DisplayableItem>>()
    private val playlistsData = MutableLiveData<List<DisplayableItem>>()
    private val foldersData = MutableLiveData<List<DisplayableItem>>()

    init {
        // all
        viewModelScope.launch {
            searchDataProvider.observe()
                .flowOn(Dispatchers.Default)
                .collect { data.value = it }
        }
        // albums
        viewModelScope.launch {
            searchDataProvider.observeAlbums()
                .flowOn(Dispatchers.Default)
                .collect { albumData.value = it }
        }
        // artists
        viewModelScope.launch {
            searchDataProvider.observeArtists()
                .flowOn(Dispatchers.Default)
                .collect { artistsData.value = it }
        }
        // genres
        viewModelScope.launch {
            searchDataProvider.observeGenres()
                .flowOn(Dispatchers.Default)
                .collect { genresData.value = it }
        }
        // playlist
        viewModelScope.launch {
            searchDataProvider.observePlaylists()
                .flowOn(Dispatchers.Default)
                .collect { playlistsData.value = it }
        }
        // folders
        viewModelScope.launch {
            searchDataProvider.observeFolders()
                .flowOn(Dispatchers.Default)
                .collect { foldersData.value = it }
        }
    }

    fun observeData(): LiveData<List<DisplayableItem>> = data
    fun observeArtistsData(): LiveData<List<DisplayableItem>> = artistsData
    fun observeAlbumsData(): LiveData<List<DisplayableItem>> = albumData
    fun observeGenresData(): LiveData<List<DisplayableItem>> = genresData
    fun observePlaylistsData(): LiveData<List<DisplayableItem>> = playlistsData
    fun observeFoldersData(): LiveData<List<DisplayableItem>> = foldersData

    override fun onCleared() {
        viewModelScope.cancel()
    }


    fun updateQuery(newQuery: String) {
        searchDataProvider.updateQuery(newQuery.trim())
    }

    fun insertToRecent(mediaId: MediaId) {
//        insertRecentUse.execute(mediaId)
//            .subscribe({}, Throwable::printStackTrace)
//            .addTo(subscriptions)
    }

    fun deleteFromRecent(mediaId: MediaId) {
//        deleteRecentSearchUseCase.execute(mediaId)
//            .subscribe({}, Throwable::printStackTrace)
//            .addTo(subscriptions)
    }

    fun clearRecentSearches() {
//        clearRecentSearchesUseCase.execute()
//            .subscribe({}, Throwable::printStackTrace)
//            .addTo(subscriptions)
    }

}