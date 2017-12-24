package dev.olog.presentation.fragment_detail.most_played

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.presentation.model.DisplayableItem
import io.reactivex.Flowable
import javax.inject.Inject

class DetailMostPlayedFragmentViewModelFactory @Inject constructor(
        private val data: Flowable<List<DisplayableItem>>

) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DetailMostPlayedFragmentViewModel(data) as T
    }
}