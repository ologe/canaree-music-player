package dev.olog.msc.domain.interactor.albums

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import io.reactivex.Flowable
import javax.inject.Inject

class AlbumsFragmentViewModelFactory @Inject constructor(
        private val mediaId: MediaId,
        private val data: Map<MediaIdCategory, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AlbumsFragmentViewModel(mediaId, data) as T
    }
}