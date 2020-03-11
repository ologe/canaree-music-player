package dev.olog.presentation.recentlyadded

import androidx.lifecycle.ViewModel
import dev.olog.core.entity.track.Song
import dev.olog.core.interactor.GetItemTitleUseCase
import dev.olog.core.interactor.ObserveRecentlyAddedUseCase
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.PresentationId
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.toDomain
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecentlyAddedFragmentViewModel @Inject constructor(
    mediaId: PresentationId.Category,
    useCase: ObserveRecentlyAddedUseCase,
    getItemTitleUseCase: GetItemTitleUseCase,
    schedulers: Schedulers

) : ViewModel() {

    val itemOrdinal = mediaId.category.ordinal // TODO try to remove ordinal

    val data: Flow<List<DisplayableTrack>> = useCase(mediaId.toDomain())
        .mapListItem { it.toRecentDetailDisplayableItem(mediaId) }
        .flowOn(schedulers.io)

    val title: Flow<String> = getItemTitleUseCase(mediaId.toDomain())
        .flowOn(schedulers.io)
        .map { it ?: "" }

    private fun Song.toRecentDetailDisplayableItem(parentId: PresentationId.Category): DisplayableTrack {
        return DisplayableTrack(
            type = R.layout.item_recently_added,
            mediaId = parentId.playableItem(id),
            title = title,
            artist = artist,
            album = album,
            idInPlaylist = idInPlaylist,
            dataModified = this.dateModified,
            duration = this.duration
        )
    }


}
