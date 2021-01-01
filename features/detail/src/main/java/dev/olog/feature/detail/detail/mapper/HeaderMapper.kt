package dev.olog.feature.detail.detail.mapper

import android.content.res.Resources
import dev.olog.domain.entity.AutoPlaylist
import dev.olog.domain.entity.track.*
import dev.olog.feature.detail.R
import dev.olog.feature.detail.detail.model.DetailFragmentModel


internal fun Folder.toHeaderItem(resources: Resources): DetailFragmentModel.MainHeader {

    return DetailFragmentModel.MainHeader(
        mediaId = getMediaId(),
        title = title,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}

internal fun Playlist.toHeaderItem(resources: Resources): DetailFragmentModel.MainHeader {
    val subtitle = if (AutoPlaylist.isAutoPlaylist(id)){
        ""
    } else {
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    }

    return DetailFragmentModel.MainHeader(
        mediaId = getMediaId(),
        title = title,
        subtitle = subtitle
    )

}

internal fun Album.toHeaderItem(): DetailFragmentModel.MainHeader {

    return DetailFragmentModel.MainHeader(
        mediaId = getMediaId(),
        title = title,
        subtitle = this.artist
    )
}

internal fun Artist.toHeaderItem(resources: Resources): DetailFragmentModel.MainHeader {

    return DetailFragmentModel.MainHeader(
        mediaId = getMediaId(),
        title = name,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.songs,
            this.songs
        ).toLowerCase()
    )
}

internal fun Genre.toHeaderItem(resources: Resources): DetailFragmentModel.MainHeader {

    return DetailFragmentModel.MainHeader(
        mediaId = getMediaId(),
        title = name,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}