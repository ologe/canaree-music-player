package dev.olog.presentation.fragment_albums

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable

class AlbumsFragmentViewModel(
        mediaId: String,
        data: Map<String, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>

) : ViewModel() {

    private val category = MediaIdHelper.extractCategory(mediaId)

    val data : LiveData<List<DisplayableItem>> = data[category]!!.asLiveData()

}