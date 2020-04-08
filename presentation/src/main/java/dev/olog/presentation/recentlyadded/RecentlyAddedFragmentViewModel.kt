package dev.olog.presentation.recentlyadded

import androidx.lifecycle.ViewModel
import dev.olog.domain.entity.track.Song
import dev.olog.domain.interactor.GetItemTitleUseCase
import dev.olog.domain.interactor.ObserveRecentlyAddedUseCase
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.presentation.R
import dev.olog.feature.presentation.base.model.DisplayableTrack
import dev.olog.feature.presentation.base.model.toDomain
import dev.olog.shared.coroutines.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
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
