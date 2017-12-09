
package dev.olog.presentation.fragment_search

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.domain.interactor.search.*
import dev.olog.presentation.model.DisplayableItem
import javax.inject.Inject

class SearchFragmentViewModelFactory @Inject constructor(
        private val application: Application,
        private val queryText: MutableLiveData<String>,
        private val searchData: LiveData<Pair<MutableMap<SearchFragmentType, MutableList<DisplayableItem>>, String>>,
        private val searchHeaders: SearchFragmentHeaders,
        private val insertSearchSongUseCase: InsertRecentSearchSongUseCase,
        private val insertSearchAlbumUseCase: InsertRecentSearchAlbumUseCase,
        private val insertSearchArtistUseCase: InsertRecentSearchArtistUseCase,
        private val deleteRecentSearchSongUseCase: DeleteRecentSearchSongUseCase,
        private val deleteRecentSearchAlbumUseCase: DeleteRecentSearchAlbumUseCase,
        private val deleteRecentSearchArtistUseCase: DeleteRecentSearchArtistUseCase,
        private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase

) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchFragmentViewModel(
                application,
                queryText,
                searchData,
                searchHeaders,
                insertSearchSongUseCase,
                insertSearchAlbumUseCase,
                insertSearchArtistUseCase,
                deleteRecentSearchSongUseCase,
                deleteRecentSearchAlbumUseCase,
                deleteRecentSearchArtistUseCase,
                clearRecentSearchesUseCase
        ) as T
    }
}