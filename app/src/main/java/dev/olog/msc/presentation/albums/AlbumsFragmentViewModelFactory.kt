package dev.olog.msc.presentation.albums

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import io.reactivex.Observable
import javax.inject.Inject

class AlbumsFragmentViewModelFactory @Inject constructor(
        private val mediaId: MediaId,
        private val data: Map<MediaIdCategory, @JvmSuppressWildcards Observable<List<DisplayableItem>>>

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AlbumsFragmentViewModel(mediaId, data) as T
    }
}