package dev.olog.presentation.fragment_detail.recently_added

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.presentation.model.DisplayableItem
import io.reactivex.Flowable
import javax.inject.Inject

class DetailRecentlyAddedFragmentViewModelFactory @Inject constructor(
        private val data: Flowable<List<DisplayableItem>>

): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DetailRecentlyAddedFragmentViewModel(data) as T
    }
}