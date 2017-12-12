package dev.olog.presentation.fragment_recently_added

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.domain.interactor.detail.recent.GetRecentlyAddedUseCase
import javax.inject.Inject

class RecentlyAddedFragmentViewModelFactory @Inject constructor(
        private val mediaId: String,
        private val useCase: GetRecentlyAddedUseCase

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RecentlyAddedFragmentViewModel(
                mediaId, useCase
        ) as T
    }
}