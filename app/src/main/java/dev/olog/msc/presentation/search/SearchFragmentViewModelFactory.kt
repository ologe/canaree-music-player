package dev.olog.msc.presentation.search

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.msc.domain.interactor.all.GetAllAlbumsUseCase
import dev.olog.msc.domain.interactor.all.GetAllArtistsUseCase
import dev.olog.msc.domain.interactor.search.delete.ClearRecentSearchesUseCase
import dev.olog.msc.domain.interactor.search.delete.DeleteRecentSearchAlbumUseCase
import dev.olog.msc.domain.interactor.search.delete.DeleteRecentSearchArtistUseCase
import dev.olog.msc.domain.interactor.search.delete.DeleteRecentSearchSongUseCase
import dev.olog.msc.domain.interactor.search.insert.InsertRecentSearchAlbumUseCase
import dev.olog.msc.domain.interactor.search.insert.InsertRecentSearchArtistUseCase
import dev.olog.msc.domain.interactor.search.insert.InsertRecentSearchSongUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import javax.inject.Inject

class SearchFragmentViewModelFactory @Inject constructor(
        private val queryText: MutableLiveData<String>,
        private val searchData: LiveData<Pair<MutableMap<SearchFragmentType, MutableList<DisplayableItem>>, String>>,
        private val searchHeaders: SearchFragmentHeaders,
        private val insertSearchSongUseCase: InsertRecentSearchSongUseCase,
        private val insertSearchAlbumUseCase: InsertRecentSearchAlbumUseCase,
        private val insertSearchArtistUseCase: InsertRecentSearchArtistUseCase,
        private val deleteRecentSearchSongUseCase: DeleteRecentSearchSongUseCase,
        private val deleteRecentSearchAlbumUseCase: DeleteRecentSearchAlbumUseCase,
        private val deleteRecentSearchArtistUseCase: DeleteRecentSearchArtistUseCase,
        private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase,
        private val getAllArtistsUseCase: GetAllArtistsUseCase,
        private val getAllAlbumsUseCase: GetAllAlbumsUseCase

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchFragmentViewModel(
                queryText,
                searchData,
                searchHeaders,
                insertSearchSongUseCase,
                insertSearchAlbumUseCase,
                insertSearchArtistUseCase,
                deleteRecentSearchSongUseCase,
                deleteRecentSearchAlbumUseCase,
                deleteRecentSearchArtistUseCase,
                clearRecentSearchesUseCase,
                getAllArtistsUseCase,
                getAllAlbumsUseCase
        ) as T
    }
}