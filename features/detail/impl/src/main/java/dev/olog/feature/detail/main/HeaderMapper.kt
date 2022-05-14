package dev.olog.feature.detail.main

import android.content.res.Resources
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Playlist
import dev.olog.feature.detail.R
import dev.olog.ui.model.DisplayableHeader


internal fun Folder.toHeaderItem(resources: Resources): DisplayableHeader {

    return DisplayableHeader(
        type = R.layout.item_detail_image,
        mediaId = getMediaId(),
        title = title,
        subtitle = resources.getQuantityString(
            localization.R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}

internal fun Playlist.toHeaderItem(resources: Resources): DisplayableHeader {
    val subtitle = if (AutoPlaylist.isAutoPlaylist(id)){
        ""
    } else {
        resources.getQuantityString(localization.R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    }

    return DisplayableHeader(
        type = R.layout.item_detail_image,
        mediaId = getMediaId(),
        title = title,
        subtitle = subtitle
    )

}

internal fun Album.toHeaderItem(): DisplayableHeader {

    return DisplayableHeader(
        type = R.layout.item_detail_image,
        mediaId = getMediaId(),
        title = title,
        subtitle = this.artist
    )
}

internal fun Artist.toHeaderItem(resources: Resources): DisplayableHeader {

    return DisplayableHeader(
        type = R.layout.item_detail_image,
        mediaId = getMediaId(),
        title = name,
        subtitle = resources.getQuantityString(localization.R.plurals.common_plurals_song, this.songs, this.songs).toLowerCase()
    )
}

internal fun Genre.toHeaderItem(resources: Resources): DisplayableHeader {

    return DisplayableHeader(
        type = R.layout.item_detail_image,
        mediaId = getMediaId(),
        title = name,
        subtitle = resources.getQuantityString(
            localization.R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}