package dev.olog.presentation.fragment_albums

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.presentation.model.DisplayableItem
import io.reactivex.Flowable
import javax.inject.Inject

class AlbumsFragmentViewModelFactory @Inject constructor(
        private val mediaId: String,
        private val data: Map<String, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AlbumsFragmentViewModel(mediaId, data) as T
    }
}