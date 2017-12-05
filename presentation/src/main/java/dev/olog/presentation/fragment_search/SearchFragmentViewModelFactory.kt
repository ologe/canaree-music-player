package dev.olog.presentation.fragment_search

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.domain.interactor.tab.GetAllAlbumsUseCase
import dev.olog.domain.interactor.tab.GetAllArtistsUseCase
import dev.olog.domain.interactor.tab.GetAllSongsUseCase
import javax.inject.Inject

class SearchFragmentViewModelFactory @Inject constructor(
        private val getAllSongsUseCase: GetAllSongsUseCase,
        private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
        private val getAllArtistsUseCase: GetAllArtistsUseCase,
        private val searchHeaders: SearchHeaders

) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchFragmentViewModel(
                getAllSongsUseCase, getAllAlbumsUseCase, getAllArtistsUseCase,
                searchHeaders
        ) as T
    }
}