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

fun Song.toDetailDisplayableItem(parentId: String): DisplayableItem{
    return DisplayableItem(
            R.layout.item_tab_song,
            MediaIdHelper.playableItem(parentId, id),
            title,
            "$artist${TextUtils.MIDDLE_DOT_SPACED}$album",
            image,
            true,
            isRemix,
            isExplicit
    )
}

fun Song.toRecentDetailDisplayableItem(parentId: String): DisplayableItem{
    return DisplayableItem(
            R.layout.item_detail_song,
            MediaIdHelper.playableItem(parentId, id), // todo
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

fun Genre.toDetailDisplayableItem(): DisplayableItem{
    return DisplayableItem(
            R.layout.item_detail_album,
            MediaIdHelper.genreId(id),
            name
    )
}