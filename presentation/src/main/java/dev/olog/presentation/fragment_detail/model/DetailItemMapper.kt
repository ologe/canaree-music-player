package dev.olog.presentation.fragment_detail.model

import android.content.res.Resources
import dev.olog.domain.entity.*
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.TextUtils

fun Folder.toHeaderItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_info_image,
            MediaIdHelper.folderId(path),
            title.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            image
    )
}

fun Playlist.toHeaderItem(resources: Resources): DisplayableItem {
    val listSize = if (this.size == -1){ "" } else {
        resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase()
    }

    return DisplayableItem(
            R.layout.item_detail_info_image,
            MediaIdHelper.playlistId(this.id),
            title.capitalize(),
            listSize,
            image
    )
}

fun Album.toHeaderItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_info_image,
            MediaIdHelper.albumId(this.id),
            title,
            artist,
            image
    )
}

fun Artist.toHeaderItem(resources: Resources): DisplayableItem {
    val songs = resources.getQuantityString(R.plurals.song_count, this.songs, this.songs)
    val albums = if (this.albums == 0) "" else {
        "${resources.getQuantityString(R.plurals.album_count, this.albums, this.albums)}${TextUtils.MIDDLE_DOT_SPACED}"
    }

    return DisplayableItem(
            R.layout.item_detail_info_image,
            MediaIdHelper.artistId(this.id),
            name,
            "$albums$songs".toLowerCase(),
            image
    )
}

fun Genre.toHeaderItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_info_image,
            MediaIdHelper.genreId(this.id),
            name,
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            image
    )
}