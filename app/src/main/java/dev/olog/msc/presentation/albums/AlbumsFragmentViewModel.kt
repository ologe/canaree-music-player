package dev.olog.msc.presentation.albums

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.k.extension.asLiveData
import io.reactivex.Observable

class AlbumsFragmentViewModel(
        mediaId: MediaId,
        data: Map<MediaIdCategory, @JvmSuppressWildcards Observable<List<DisplayableItem>>>

) : ViewModel() {


    val data : LiveData<List<DisplayableItem>> = data[mediaId.category]!!.asLiveData()

}