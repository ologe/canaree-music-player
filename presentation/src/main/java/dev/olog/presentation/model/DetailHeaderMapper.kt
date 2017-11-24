package dev.olog.presentation.model

import dev.olog.domain.entity.*
import dev.olog.presentation.R

private const val HEADER_ID = "header media id"

fun Folder.toHeaderItem(): DisplayableItem{
    return DisplayableItem(
            R.layout.item_detail_info,
            HEADER_ID,
            title.capitalize(),
            null,
            image
    )
}

fun Playlist.toHeaderItem(): DisplayableItem{
    return DisplayableItem(
            R.layout.item_detail_info,
            HEADER_ID,
            title.capitalize(),
            null,
            image
    )
}

fun Album.toHeaderItem(): DisplayableItem{
    return DisplayableItem(
            R.layout.item_detail_info,
            HEADER_ID,
            title,
            artist,
            image
    )
}

fun Artist.toHeaderItem(): DisplayableItem{
    return DisplayableItem(
            R.layout.item_detail_info,
            HEADER_ID,
            name,
            null,
            image
    )
}

fun Genre.toHeaderItem(): DisplayableItem{
    return DisplayableItem(
            R.layout.item_detail_info,
            HEADER_ID,
            name,
            null,
            image
    )
}