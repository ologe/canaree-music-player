package dev.olog.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.MediaId
import dev.olog.core.interactor.search.ClearRecentSearchesUseCase
import dev.olog.core.interactor.search.DeleteRecentSearchUseCase
import dev.olog.core.interactor.search.InsertRecentSearchUseCase
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.model.DisplayableItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchFragmentViewModel @Inject constructor(
    private val dataProvider: SearchDataProvider,
    private val insertRecentUse: InsertRecentSearchUseCase,
    private val deleteRecentSearchUseCase: DeleteRecentSearchUseCase,
    private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase,
    private val schedulers: Schedulers

) : ViewModel() {

    val data: Flow<List<DisplayableItem>> = dataProvider.observe()
        .flowOn(schedulers.cpu)

    val artistsData: Flow<List<DisplayableItem>> = dataProvider.observeArtists()
        .flowOn(schedulers.cpu)

    val albumsData: Flow<List<DisplayableItem>> = dataProvider.observeAlbums()
        .flowOn(schedulers.cpu)

    val genresData: Flow<List<DisplayableItem>> = dataProvider.observeGenres()
        .flowOn(schedulers.cpu)

    val playlistsData: Flow<List<DisplayableItem>> = dataProvider.observePlaylists()
        .flowOn(schedulers.cpu)

    val foldersData: Flow<List<DisplayableItem>> = dataProvider.observeFolders()
        .flowOn(schedulers.cpu)
    

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