package dev.olog.presentation.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.MediaId
import dev.olog.core.interactor.search.ClearRecentSearchesUseCase
import dev.olog.core.interactor.search.DeleteRecentSearchUseCase
import dev.olog.core.interactor.search.InsertRecentSearchUseCase
import dev.olog.presentation.model.DisplayableItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchFragmentViewModel @ViewModelInject constructor(
    private val dataProvider: SearchDataProvider,
    private val insertRecentUse: InsertRecentSearchUseCase,
    private val deleteRecentSearchUseCase: DeleteRecentSearchUseCase,
    private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase

) : ViewModel() {

    private val data = MutableStateFlow<List<DisplayableItem>>(emptyList())
    private val albumData = MutableStateFlow<List<DisplayableItem>>(emptyList())
    private val artistsData = MutableStateFlow<List<DisplayableItem>>(emptyList())
    private val genresData = MutableStateFlow<List<DisplayableItem>>(emptyList())
    private val playlistsData = MutableStateFlow<List<DisplayableItem>>(emptyList())
    private val foldersData = MutableStateFlow<List<DisplayableItem>>(emptyList())

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

    fun observeData(): Flow<List<DisplayableItem>> = data
    fun observeArtistsData(): Flow<List<DisplayableItem>> = artistsData
    fun observeAlbumsData(): Flow<List<DisplayableItem>> = albumData
    fun observeGenresData(): Flow<List<DisplayableItem>> = genresData
    fun observePlaylistsData(): Flow<List<DisplayableItem>> = playlistsData
    fun observeFoldersData(): Flow<List<DisplayableItem>> = foldersData

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