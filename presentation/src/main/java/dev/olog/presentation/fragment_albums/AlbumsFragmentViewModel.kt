package dev.olog.presentation.fragment_albums

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.shared.MediaId
import dev.olog.shared.MediaIdCategory
import io.reactivex.Flowable

class AlbumsFragmentViewModel(
        mediaId: MediaId,
        data: Map<MediaIdCategory, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>

) : ViewModel() {



    val data : LiveData<List<DisplayableItem>> = data[mediaId.category]!!.asLiveData()

}