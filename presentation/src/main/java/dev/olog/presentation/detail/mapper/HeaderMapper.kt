package dev.olog.presentation.detail.mapper

import android.content.res.Resources
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.track.*
import dev.olog.presentation.R
import dev.olog.presentation.detail.adapter.DetailFragmentItem


internal fun Folder.toHeaderItem(resources: Resources): DetailFragmentItem.Header {
    return DetailFragmentItem.Header(
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

internal fun Playlist.toHeaderItem(resources: Resources): DetailFragmentItem.Header {
    val subtitle = if (AutoPlaylist.isAutoPlaylist(id)){
        ""
    } else {
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    }

    return DetailFragmentItem.Header(
        mediaId = getMediaId(),
        title = title,
        subtitle = subtitle,
        biography = null,
    )

}

internal fun Album.toHeaderItem(): DetailFragmentItem.Header {

    return DetailFragmentItem.Header(
        mediaId = getMediaId(),
        title = title,
        subtitle = this.artist,
        biography = null,
    )
}

internal fun Artist.toHeaderItem(resources: Resources): DetailFragmentItem.Header {

    return DetailFragmentItem.Header(
        mediaId = getMediaId(),
        title = name,
        subtitle = resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs).toLowerCase(),
        biography = null,
    )
}

internal fun Genre.toHeaderItem(resources: Resources): DetailFragmentItem.Header {

    return DetailFragmentItem.Header(
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