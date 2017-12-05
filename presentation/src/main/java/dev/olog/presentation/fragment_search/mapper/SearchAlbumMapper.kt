package dev.olog.presentation.fragment_search.mapper

import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper

fun Album.toSearchDisplayableItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_search_album,
            MediaIdHelper.albumId(id),
            title,
            artist,
            image
    )
}

fun Artist.toSearchDisplayableItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_search_album,
            MediaIdHelper.artistId(id),
            name,
            null,
            image
    )
}