package dev.olog.presentation.fragment_search

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.domain.interactor.search.*
import dev.olog.domain.interactor.tab.GetAllAlbumsUseCase
import dev.olog.domain.interactor.tab.GetAllArtistsUseCase
import dev.olog.domain.interactor.tab.GetAllSongsUseCase
import javax.inject.Inject

class SearchFragmentViewModelFactory @Inject constructor(
        private val application: Application,
        private val getAllSongsUseCase: GetAllSongsUseCase,
        private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
        private val getAllArtistsUseCase: GetAllArtistsUseCase,
        private val searchHeaders: SearchHeaders,
        private val getAllRecentSearchesUseCase: GetAllRecentSearchesUseCase,
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
                getAllSongsUseCase, getAllAlbumsUseCase, getAllArtistsUseCase,
                searchHeaders,
                getAllRecentSearchesUseCase,
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