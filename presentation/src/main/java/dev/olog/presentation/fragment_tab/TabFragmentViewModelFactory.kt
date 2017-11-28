package dev.olog.presentation.fragment_tab

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.presentation.model.DisplayableItem
import io.reactivex.Flowable
import javax.inject.Inject

class TabFragmentViewModelFactory @Inject constructor(
        private val data: Map<Int, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TabFragmentViewModel(data) as T
    }
}
