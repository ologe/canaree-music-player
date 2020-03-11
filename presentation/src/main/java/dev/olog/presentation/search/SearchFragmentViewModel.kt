package dev.olog.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.interactor.search.ClearRecentSearchesUseCase
import dev.olog.core.interactor.search.DeleteRecentSearchUseCase
import dev.olog.core.interactor.search.InsertRecentSearchUseCase
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.PresentationId
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.toDomain
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

    val artistsData: Flow<List<DisplayableAlbum>> = dataProvider.observeArtists()
        .flowOn(schedulers.cpu)

    val albumsData: Flow<List<DisplayableAlbum>> = dataProvider.observeAlbums()
        .flowOn(schedulers.cpu)

    val genresData: Flow<List<DisplayableAlbum>> = dataProvider.observeGenres()
        .flowOn(schedulers.cpu)

    val playlistsData: Flow<List<DisplayableAlbum>> = dataProvider.observePlaylists()
        .flowOn(schedulers.cpu)

    val foldersData: Flow<List<DisplayableAlbum>> = dataProvider.observeFolders()
        .flowOn(schedulers.cpu)
    

    fun updateQuery(newQuery: String) {
        dataProvider.updateQuery(newQuery.trim())
    }

    fun insertToRecent(mediaId: PresentationId) = viewModelScope.launch(schedulers.io) {
        insertRecentUse(mediaId.toDomain())
    }

    fun deleteFromRecent(mediaId: PresentationId) = viewModelScope.launch(schedulers.io) {
        deleteRecentSearchUseCase(mediaId.toDomain())
    }

    fun clearRecentSearches() = viewModelScope.launch(schedulers.io) {
        clearRecentSearchesUseCase()
    }

}