package dev.olog.presentation.fragment_recently_added

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import dev.olog.domain.entity.Song
import dev.olog.domain.interactor.detail.recent.GetRecentlyAddedUseCase
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.groupMap

class RecentlyAddedFragmentViewModel(
        mediaId: String,
        useCase: GetRecentlyAddedUseCase

) : ViewModel() {

    val data : LiveData<List<DisplayableItem>> = useCase.execute(mediaId)
            .groupMap { it.toRecentDetailDisplayableItem(mediaId) }
            .asLiveData()

}

private fun Song.toRecentDetailDisplayableItem(parentId: String): DisplayableItem {
    return DisplayableItem(
            R.layout.item_recently_added,
            MediaIdHelper.playableItem(parentId, id),
            title,
            artist,
            image,
            true,
            isRemix,
            isExplicit
    )
}


