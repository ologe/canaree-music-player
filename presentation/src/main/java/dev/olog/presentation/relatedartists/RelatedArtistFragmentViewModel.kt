package dev.olog.presentation.relatedartists

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.interactor.GetItemTitleUseCase
import dev.olog.core.interactor.ObserveRelatedArtistsUseCase
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RelatedArtistFragmentViewModel @Inject constructor(
    resources: Resources,
    mediaId: MediaId,
    useCase: ObserveRelatedArtistsUseCase,
    getItemTitleUseCase: GetItemTitleUseCase,
    schedulers: Schedulers

) : ViewModel() {

    val itemOrdinal = mediaId.category.ordinal

    val data: Flow<List<DisplayableItem>> = useCase(mediaId)
        .mapListItem { it.toRelatedArtist(resources) }
        .flowOn(schedulers.io)

    val title: Flow<String> = getItemTitleUseCase(mediaId)
        .flowOn(schedulers.io)
        .map { it ?: "" }


    private fun Artist.toRelatedArtist(resources: Resources): DisplayableItem {
        val songs =
            resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs)

        return DisplayableAlbum(
            type = R.layout.item_related_artist,
            mediaId = getMediaId(),
            title = this.name,
            subtitle = songs
        )
    }

}