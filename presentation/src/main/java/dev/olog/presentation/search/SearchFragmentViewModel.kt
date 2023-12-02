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
import dev.olog.presentation.search.adapter.SearchFragmentItem
import kotlinx.coroutines.Dispatchers
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

    private val data = MutableLiveData<List<SearchFragmentItem>>()

    init {
        viewModelScope.launch {
            dataProvider.observe()
                .flowOn(Dispatchers.Default)
                .collect { data.value = it }
        }
    }

    fun observeData(): LiveData<List<SearchFragmentItem>> = data

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