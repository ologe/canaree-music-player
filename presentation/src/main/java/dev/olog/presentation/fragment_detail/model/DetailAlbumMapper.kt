package dev.olog.presentation.fragment_detail.model

import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Folder
import dev.olog.domain.entity.Genre
import dev.olog.domain.entity.Playlist
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper

fun Folder.toDetailDisplayableItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album_mini,
            MediaIdHelper.folderId(path),
            title.capitalize()
    )
}

fun Playlist.toDetailDisplayableItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album_mini,
            MediaIdHelper.playlistId(id),
            title.capitalize()
    )
}

fun Album.toDetailDisplayableItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album,
            MediaIdHelper.albumId(id),
            title,
            artist,
            image
    )
}

fun Genre.toDetailDisplayableItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album_mini,
            MediaIdHelper.genreId(id),
            name.capitalize()
    )
}