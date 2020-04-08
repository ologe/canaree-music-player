package dev.olog.presentation.relatedartists

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import dev.olog.domain.entity.track.Artist
import dev.olog.domain.interactor.GetItemTitleUseCase
import dev.olog.domain.interactor.ObserveRelatedArtistsUseCase
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.presentationId
import dev.olog.feature.presentation.base.model.toDomain
import dev.olog.shared.coroutines.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RelatedArtistFragmentViewModel @Inject constructor(
    resources: Resources,
    mediaId: PresentationId.Category,
    useCase: ObserveRelatedArtistsUseCase,
    getItemTitleUseCase: GetItemTitleUseCase,
    schedulers: Schedulers

) : ViewModel() {

    val itemOrdinal = mediaId.category.ordinal // TODO try to remove ordinal

    val data: Flow<List<DisplayableAlbum>> = useCase(mediaId.toDomain())
        .mapListItem { it.toRelatedArtist(resources) }
        .flowOn(schedulers.io)

    val title: Flow<String> = getItemTitleUseCase(mediaId.toDomain())
        .flowOn(schedulers.io)


    private fun Artist.toRelatedArtist(resources: Resources): DisplayableAlbum {
        val songs =
            resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs)

        return DisplayableAlbum(
            type = R.layout.item_related_artist,
            mediaId = presentationId,
            title = this.name,
            subtitle = songs
        )
    }

}