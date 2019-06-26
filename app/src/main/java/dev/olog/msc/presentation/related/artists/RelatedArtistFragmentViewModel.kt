package dev.olog.msc.presentation.related.artists

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.getMediaId
import dev.olog.msc.R
import dev.olog.msc.domain.interactor.GetItemTitleUseCase
import dev.olog.msc.domain.interactor.all.related.artists.ObserveRelatedArtistsUseCase
import dev.olog.shared.extensions.asLiveData
import dev.olog.msc.utils.safeCompare
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.extensions.mapToList
import dev.olog.shared.utils.TextUtils
import kotlinx.coroutines.rx2.asFlowable
import java.text.Collator
import javax.inject.Inject

class RelatedArtistFragmentViewModel @Inject constructor(
    resources: Resources,
    mediaId: MediaId,
    useCase: ObserveRelatedArtistsUseCase,
    getItemTitleUseCase: GetItemTitleUseCase,
    collator: Collator

): ViewModel() {

    val itemOrdinal = mediaId.category.ordinal

    val data: LiveData<List<DisplayableItem>> = useCase(mediaId)
        .asFlowable().toObservable()
            .mapToList { it.toRelatedArtist(resources) }
            .map { it.sortedWith(Comparator { o1, o2 -> collator.safeCompare(o1.title, o2.title) }) }
            .asLiveData()

    val itemTitle = getItemTitleUseCase.execute(mediaId).asLiveData()

    private fun Artist.toRelatedArtist(resources: Resources): DisplayableItem {
        val songs = resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs)
        val albums = if (this.albums == 0) "" else {
            "${resources.getQuantityString(R.plurals.common_plurals_album, this.albums, this.albums)}${TextUtils.MIDDLE_DOT_SPACED}"
        }

        return DisplayableItem(
            R.layout.item_related_artist,
            getMediaId(),
            this.name,
            "$albums$songs"
        )
    }

}