package dev.olog.presentation.recentlyadded

import androidx.lifecycle.ViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song
import dev.olog.core.interactor.GetItemTitleUseCase
import dev.olog.core.interactor.ObserveRecentlyAddedUseCase
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecentlyAddedFragmentViewModel @Inject constructor(
    mediaId: MediaId,
    useCase: ObserveRecentlyAddedUseCase,
    getItemTitleUseCase: GetItemTitleUseCase,
    schedulers: Schedulers

) : ViewModel() {

    val itemOrdinal = mediaId.category.ordinal

    val data: Flow<List<DisplayableItem>> = useCase(mediaId)
        .mapListItem { it.toRecentDetailDisplayableItem(mediaId) }
        .flowOn(schedulers.io)

    val title: Flow<String> = getItemTitleUseCase(mediaId)
        .flowOn(schedulers.io)
        .map { it ?: "" }

    private fun Song.toRecentDetailDisplayableItem(parentId: MediaId): DisplayableItem {
        return DisplayableTrack(
            type = R.layout.item_recently_added,
            mediaId = MediaId.playableItem(parentId, id),
            title = title,
            artist = artist,
            album = album,
            idInPlaylist = idInPlaylist,
            dataModified = this.dateModified,
            duration = this.duration
        )
    }


}
