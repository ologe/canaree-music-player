package dev.olog.presentation.model

import dev.olog.domain.entity.*
import dev.olog.presentation.R
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.TextUtils

fun Folder.toDetailDisplayableItem(): DisplayableItem{
    return DisplayableItem(
            R.layout.item_detail_album,
            MediaIdHelper.folderId(path),
            title.capitalize()
    )
}

fun Playlist.toDetailDisplayableItem(): DisplayableItem{
    return dev.olog.presentation.model.DisplayableItem(
            R.layout.item_detail_album,
            MediaIdHelper.playlistId(id),
            title.capitalize()
    )
}

fun Song.toDetailDisplayableItem(): DisplayableItem{
    return DisplayableItem(
            R.layout.item_detail_song,
            MediaIdHelper.songId(id),
            title,
            "$artist${TextUtils.MIDDLE_DOT_SPACED}$album",
            image,
            true,
            isRemix,
            isExplicit
    )
}

fun Album.toDetailDisplayableItem(): DisplayableItem{
    return DisplayableItem(
            R.layout.item_detail_album,
            MediaIdHelper.albumId(id),
            title,
            artist,
            image
    )
}

fun Artist.toDetailDisplayableItem(): DisplayableItem{
    return DisplayableItem(
            R.layout.item_detail_album,
            MediaIdHelper.artistId(id),
            name
    )
}

fun Genre.toDetailDisplayableItem(): DisplayableItem{
    return DisplayableItem(
            R.layout.item_detail_album,
            MediaIdHelper.genreId(id),
            name
    )
}