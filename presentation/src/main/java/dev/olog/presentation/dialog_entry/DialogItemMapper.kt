package dev.olog.presentation.dialog_entry

import dev.olog.domain.entity.*
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem

private const val HEADER_ID = "header media id"

fun Folder.toDialogItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_dialog_image,
            HEADER_ID,
            title.capitalize(),
            null,
            image
    )
}

fun Playlist.toDialogItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_dialog_image,
            HEADER_ID,
            title.capitalize(),
            null,
            image
    )
}

fun Song.toDialogItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_dialog_image,
            HEADER_ID,
            title.capitalize(),
            artist,
            image
    )
}

fun Album.toDialogItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_dialog_image,
            HEADER_ID,
            title,
            artist,
            image
    )
}

fun Artist.toDialogItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_dialog_image,
            HEADER_ID,
            name,
            null,
            image
    )
}

fun Genre.toDialogItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_dialog_image,
            HEADER_ID,
            name,
            null,
            image
    )
}