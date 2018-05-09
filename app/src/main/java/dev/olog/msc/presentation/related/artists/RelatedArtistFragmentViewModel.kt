package dev.olog.msc.presentation.related.artists

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.content.res.Resources
import dev.olog.msc.R
import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.interactor.all.related.artists.GetRelatedArtistsUseCase
import dev.olog.msc.domain.interactor.GetItemTitleUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.k.extension.asLiveData
import dev.olog.msc.utils.k.extension.mapToList
import java.text.Collator

class RelatedArtistViewModel(
        resources: Resources,
        mediaId: MediaId,
        useCase: GetRelatedArtistsUseCase,
        getItemTitleUseCase: GetItemTitleUseCase,
        collator: Collator

): ViewModel() {

    val itemOrdinal = mediaId.category.ordinal

    val data: LiveData<List<DisplayableItem>> = useCase.execute(mediaId)
            .mapToList { it.toRelatedArtist(resources) }
            .map { it.sortedWith(Comparator { o1, o2 -> collator.compare(o1.title, o2.title) }) }
            .asLiveData()

    val itemTitle = getItemTitleUseCase.execute(mediaId).asLiveData()

}

private fun Artist.toRelatedArtist(resources: Resources): DisplayableItem {
    val songs = resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs)
    val albums = if (this.albums == 0) "" else {
        "${resources.getQuantityString(R.plurals.common_plurals_album, this.albums, this.albums)}${TextUtils.MIDDLE_DOT_SPACED}"
    }

    return DisplayableItem(
            R.layout.item_related_artist,
            MediaId.artistId(id),
            this.name,
            "$albums$songs",
            this.image
    )
}