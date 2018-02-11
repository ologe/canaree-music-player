package dev.olog.msc.presentation.recently.added

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import dev.olog.msc.R
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.detail.recent.GetRecentlyAddedUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.asLiveData
import dev.olog.msc.utils.k.extension.groupMap

class RecentlyAddedFragmentViewModel(
        mediaId: MediaId,
        useCase: GetRecentlyAddedUseCase

) : ViewModel() {

    val data : LiveData<List<DisplayableItem>> = useCase.execute(mediaId)
            .groupMap { it.toRecentDetailDisplayableItem(mediaId) }
            .asLiveData()

}

private fun Song.toRecentDetailDisplayableItem(parentId: MediaId): DisplayableItem {
    return DisplayableItem(
            R.layout.item_recently_added,
            MediaId.playableItem(parentId, id),
            title,
            artist,
            image,
            true,
            isRemix,
            isExplicit
    )
}


