package dev.olog.presentation.detail.mapper

import android.content.res.Resources
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Playlist
import dev.olog.presentation.R
import dev.olog.presentation.detail.adapter.DetailItem


internal fun Folder.toHeaderItem(resources: Resources): DetailItem.DetailHeader {
    return DetailItem.DetailHeader(
        mediaId = getMediaId(),
        title = title,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase(),
        biography = null,
    )
}

internal fun Playlist.toHeaderItem(resources: Resources): DetailItem.DetailHeader {
    val subtitle = if (AutoPlaylist.isAutoPlaylist(id)){
        "" // TODO
    } else {
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    }

    return DetailItem.DetailHeader(
        mediaId = getMediaId(),
        title = title,
        subtitle = subtitle,
        biography = null,
    )

}

internal fun Album.toHeaderItem(): DetailItem.DetailHeader {

    return DetailItem.DetailHeader(
        mediaId = getMediaId(),
        title = title,
        subtitle = this.artist,
        biography = null,
    )
}

internal fun Artist.toHeaderItem(resources: Resources): DetailItem.DetailHeader {

    return DetailItem.DetailHeader(
        mediaId = getMediaId(),
        title = name,
        subtitle = resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs).toLowerCase(),
        biography = null,
    )
}

internal fun Genre.toHeaderItem(resources: Resources): DetailItem.DetailHeader {

    return DetailItem.DetailHeader(
        mediaId = getMediaId(),
        title = name,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase(),
        biography = null,
    )
}