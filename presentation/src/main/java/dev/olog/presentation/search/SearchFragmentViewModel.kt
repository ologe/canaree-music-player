package dev.olog.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.MediaId
import dev.olog.core.interactor.search.ClearRecentSearchesUseCase
import dev.olog.core.interactor.search.DeleteRecentSearchUseCase
import dev.olog.core.interactor.search.InsertRecentSearchUseCase
import dev.olog.core.schedulers.Schedulers
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
    private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase,
    private val schedulers: Schedulers

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
            .flowOn(schedulers.cpu)
            .onEach { data.value = it }
            .launchIn(viewModelScope)
        
        // albums
        dataProvider.observeAlbums()
            .flowOn(schedulers.cpu)
            .onEach { albumData.value = it }
            .launchIn(viewModelScope)
        // artists
        dataProvider.observeArtists()
            .flowOn(schedulers.cpu)
            .onEach { artistsData.value = it }
            .launchIn(viewModelScope)
        // genres
        dataProvider.observeGenres()
            .flowOn(schedulers.cpu)
            .onEach { genresData.value = it }
            .launchIn(viewModelScope)
        // playlist
        dataProvider.observePlaylists()
            .flowOn(schedulers.cpu)
            .onEach { playlistsData.value = it }
            .launchIn(viewModelScope)
        // folders
        dataProvider.observeFolders()
            .flowOn(schedulers.cpu)
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

    fun insertToRecent(mediaId: MediaId) = viewModelScope.launch(schedulers.io) {
        insertRecentUse(mediaId)
    }

    fun deleteFromRecent(mediaId: MediaId) = viewModelScope.launch(schedulers.io) {
        deleteRecentSearchUseCase(mediaId)
    }

    fun clearRecentSearches() = viewModelScope.launch(schedulers.io) {
        clearRecentSearchesUseCase()
    }

}