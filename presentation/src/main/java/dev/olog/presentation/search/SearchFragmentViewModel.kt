package dev.olog.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.MediaId
import dev.olog.core.interactor.search.ClearRecentSearchesUseCase
import dev.olog.core.interactor.search.DeleteRecentSearchUseCase
import dev.olog.core.interactor.search.InsertRecentSearchUseCase
import dev.olog.presentation.model.DisplayableItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

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
        dataProvider.observe()
            .flowOn(Dispatchers.Default)
            .onEach { data.value = it }
            .launchIn(viewModelScope)
        
        // albums
        dataProvider.observeAlbums()
            .flowOn(Dispatchers.Default)
            .onEach { albumData.value = it }
            .launchIn(viewModelScope)
        // artists
        dataProvider.observeArtists()
            .flowOn(Dispatchers.Default)
            .onEach { artistsData.value = it }
            .launchIn(viewModelScope)
        // genres
        dataProvider.observeGenres()
            .flowOn(Dispatchers.Default)
            .onEach { genresData.value = it }
            .launchIn(viewModelScope)
        // playlist
        dataProvider.observePlaylists()
            .flowOn(Dispatchers.Default)
            .onEach { playlistsData.value = it }
            .launchIn(viewModelScope)
        // folders
        dataProvider.observeFolders()
            .flowOn(Dispatchers.Default)
            .onEach { foldersData.value = it }
            .launchIn(viewModelScope)
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