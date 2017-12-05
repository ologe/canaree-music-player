package dev.olog.presentation.fragment_search.mapper

import android.content.Context
import dev.olog.domain.entity.SearchResult
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.RecentSearchesTypes

fun SearchResult.toDisplayableItem(context: Context) : DisplayableItem{
    val subtitle = when (this.itemType) {
        RecentSearchesTypes.SONG -> context.getString(R.string.search_type_song)
        RecentSearchesTypes.ALBUM -> context.getString(R.string.search_type_album)
        RecentSearchesTypes.ARTIST -> context.getString(R.string.search_type_artist)
        else -> throw IllegalArgumentException("invalid item type $itemType")
    }

    val isPlayable = this.itemType == RecentSearchesTypes.SONG

    return DisplayableItem(
            R.layout.item_recent_search,
            this.mediaId,
            this.title,
            subtitle,
            this.image,
            isPlayable
    )
}