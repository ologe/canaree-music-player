package dev.olog.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.interactor.search.ClearRecentSearchesUseCase
import dev.olog.core.interactor.search.DeleteRecentSearchUseCase
import dev.olog.core.interactor.search.InsertRecentSearchUseCase
import dev.olog.presentation.model.DisplayableItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchFragmentViewModel @Inject constructor(
    private val dataProvider: SearchDataProvider,
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
            dataProvider.observe()
                .flowOn(Dispatchers.Default)
                .collect { data.value = it }
        }
        // albums
        viewModelScope.launch {
            dataProvider.observeAlbums()
                .flowOn(Dispatchers.Default)
                .collect { albumData.value = it }
        }
        // artists
        viewModelScope.launch {
            dataProvider.observeArtists()
                .flowOn(Dispatchers.Default)
                .collect { artistsData.value = it }
        }
        // genres
        viewModelScope.launch {
            dataProvider.observeGenres()
                .flowOn(Dispatchers.Default)
                .collect { genresData.value = it }
        }
        // playlist
        viewModelScope.launch {
            dataProvider.observePlaylists()
                .flowOn(Dispatchers.Default)
                .collect { playlistsData.value = it }
        }
        // folders
        viewModelScope.launch {
            dataProvider.observeFolders()
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

    fun updateQuery(newQuery: String) {
        dataProvider.updateQuery(newQuery.trim())
    }

    fun insertToRecent(mediaId: MediaId) = viewModelScope.launch(Dispatchers.IO) {
        insertRecentUse(mediaId)
    }

    fun deleteFromRecent(mediaId: MediaId) = viewModelScope.launch(Dispatchers.IO) {
        deleteRecentSearchUseCase(mediaId)
    }

    fun clearRecentSearches() = viewModelScope.launch(Dispatchers.IO) {
        clearRecentSearchesUseCase()
    }

}