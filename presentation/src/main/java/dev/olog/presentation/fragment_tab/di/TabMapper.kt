package dev.olog.presentation.fragment_tab.di

import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper

fun Album.toLastPlayedDisplayableItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_tab_album_last_played,
            MediaIdHelper.albumId(id),
            title,
            artist,
            image
    )
}

fun Artist.toLastPlayedDisplayableItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_tab_album_last_played,
            MediaIdHelper.artistId(id),
            name,
            null
    )
}