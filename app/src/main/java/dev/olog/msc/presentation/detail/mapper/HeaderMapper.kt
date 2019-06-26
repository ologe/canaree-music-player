package dev.olog.msc.presentation.detail.mapper

import android.content.res.Resources
import dev.olog.core.entity.track.*
import dev.olog.msc.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.utils.TextUtils


internal fun Folder.toHeaderItem(resources: Resources): DisplayableItem {

    return DisplayableItem(
        R.layout.item_detail_item_image,
        getMediaId(),
        title,
        subtitle = resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    )
}

internal fun Playlist.toHeaderItem(resources: Resources): DisplayableItem {
    val listSize = if (this.size == -1) {
        ""
    } else {
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    }

    return DisplayableItem(
        R.layout.item_detail_item_image,
        getMediaId(),
        title,
        listSize
    )

}

internal fun Album.toHeaderItem(): DisplayableItem {

    return DisplayableItem(
        R.layout.item_detail_item_image,
        getMediaId(),
        title,
        DisplayableItem.adjustArtist(this.artist)
    )
}

internal fun Artist.toHeaderItem(resources: Resources): DisplayableItem {
    val songs = resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs)
    val albums = if (this.albums == 0) "" else {
        "${resources.getQuantityString(
            R.plurals.common_plurals_album,
            this.albums,
            this.albums
        )}${TextUtils.MIDDLE_DOT_SPACED}"
    }

    return DisplayableItem(
        R.layout.item_detail_item_image,
        getMediaId(),
        name,
        "$albums$songs".toLowerCase()
    )
}

internal fun Genre.toHeaderItem(resources: Resources): DisplayableItem {

    return DisplayableItem(
        R.layout.item_detail_item_image,
        getMediaId(),
        name,
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    )
}