package dev.olog.presentation.fragment_recently_added

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import dev.olog.domain.entity.Song
import dev.olog.domain.interactor.detail.recent.GetRecentlyAddedUseCase
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaId
import dev.olog.shared.groupMap
import dev.olog.shared_android.extension.asLiveData

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


