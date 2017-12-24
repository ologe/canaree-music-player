package dev.olog.presentation.fragment_detail.recently_added

import android.arch.lifecycle.ViewModel
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.extension.asLiveData
import io.reactivex.Flowable

class DetailRecentlyAddedFragmentViewModel(
        data: Flowable<List<DisplayableItem>>

) : ViewModel() {

    val data = data.asLiveData()

}