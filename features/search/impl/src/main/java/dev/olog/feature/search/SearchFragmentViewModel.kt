package dev.olog.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.interactor.search.ClearRecentSearchesUseCase
import dev.olog.core.interactor.search.DeleteRecentSearchUseCase
import dev.olog.core.interactor.search.InsertRecentSearchUseCase
import dev.olog.feature.search.model.SearchState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchFragmentViewModel @Inject constructor(
    private val dataProvider: SearchDataProvider,
    private val insertRecentUse: InsertRecentSearchUseCase,
    private val deleteRecentSearchUseCase: DeleteRecentSearchUseCase,
    private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase,
) : ViewModel() {

    val data: Flow<SearchState>
        get() = dataProvider.observe()

    val query: StateFlow<String>
        get() = dataProvider.query

    fun updateQuery(newQuery: String) {
        dataProvider.updateQuery(newQuery)
    }

    fun clearQuery() {
        updateQuery("")
    }

    fun insertToRecent(mediaId: MediaId) {
        viewModelScope.launch {
            insertRecentUse(mediaId)
        }
    }

    fun deleteFromRecent(mediaId: MediaId) {
        viewModelScope.launch {
            deleteRecentSearchUseCase(mediaId)
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            clearRecentSearchesUseCase()
        }
    }

}