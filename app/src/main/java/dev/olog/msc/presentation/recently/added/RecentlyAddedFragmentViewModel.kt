package dev.olog.msc.presentation.recently.added

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song
import dev.olog.msc.R
import dev.olog.msc.domain.interactor.GetItemTitleUseCase
import dev.olog.msc.domain.interactor.all.recently.added.GetRecentlyAddedUseCase
import dev.olog.shared.extensions.asLiveData
import dev.olog.shared.mapToList
import dev.olog.presentation.model.DisplayableItem
import javax.inject.Inject

class RecentlyAddedFragmentViewModel @Inject constructor(
    mediaId: MediaId,
    useCase: GetRecentlyAddedUseCase,
    getItemTitleUseCase: GetItemTitleUseCase

) : ViewModel() {

    val itemOrdinal = mediaId.category.ordinal

    val data : LiveData<List<DisplayableItem>> = useCase.execute(mediaId)
            .mapToList { it.toRecentDetailDisplayableItem(mediaId) }
            .asLiveData()

    val itemTitle = getItemTitleUseCase.execute(mediaId).asLiveData()

}

private fun Song.toRecentDetailDisplayableItem(parentId: MediaId): DisplayableItem {
    return DisplayableItem(
        R.layout.item_recently_added,
        MediaId.playableItem(parentId, id),
        title,
        DisplayableItem.adjustArtist(artist),
        true
    )
}


