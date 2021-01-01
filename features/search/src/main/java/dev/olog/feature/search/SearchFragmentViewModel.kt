package dev.olog.feature.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.domain.gateway.RecentSearchesGateway
import dev.olog.domain.interactor.search.DeleteRecentSearchUseCase
import dev.olog.domain.interactor.search.InsertRecentSearchUseCase
import dev.olog.domain.mediaid.MediaId
import dev.olog.feature.search.model.SearchDataProvider
import dev.olog.feature.search.model.SearchFragmentModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal class SearchFragmentViewModel @ViewModelInject constructor(
    private val dataProvider: SearchDataProvider,
    private val insertRecentUse: InsertRecentSearchUseCase,
    private val deleteRecentSearchUseCase: DeleteRecentSearchUseCase,
    private val recentSearchesGateway: RecentSearchesGateway
) : ViewModel() {

    private val data = MutableStateFlow<List<SearchFragmentModel>>(emptyList())
    private val albumData = MutableStateFlow<List<SearchFragmentModel.Album>>(emptyList())
    private val artistsData = MutableStateFlow<List<SearchFragmentModel.Album>>(emptyList())
    private val genresData = MutableStateFlow<List<SearchFragmentModel.Album>>(emptyList())
    private val playlistsData = MutableStateFlow<List<SearchFragmentModel.Album>>(emptyList())
    private val foldersData = MutableStateFlow<List<SearchFragmentModel.Album>>(emptyList())

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

    fun observeData(): Flow<List<SearchFragmentModel>> = data
    fun observeArtistsData(): Flow<List<SearchFragmentModel.Album>> = artistsData
    fun observeAlbumsData(): Flow<List<SearchFragmentModel.Album>> = albumData
    fun observeGenresData(): Flow<List<SearchFragmentModel.Album>> = genresData
    fun observePlaylistsData(): Flow<List<SearchFragmentModel.Album>> = playlistsData
    fun observeFoldersData(): Flow<List<SearchFragmentModel.Album>> = foldersData

    fun updateQuery(newQuery: String) {
        dataProvider.updateQuery(newQuery.trim())
    }

    fun insertToRecent(mediaId: MediaId) = viewModelScope.launch {
        insertRecentUse(mediaId)
    }

    fun deleteFromRecent(mediaId: MediaId) = viewModelScope.launch {
        deleteRecentSearchUseCase(mediaId)
    }

    fun clearRecentSearches() = viewModelScope.launch {
        recentSearchesGateway.deleteAll()
    }

}