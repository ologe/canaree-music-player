package dev.olog.feature.library.tab

import android.content.res.Resources
import dev.olog.domain.entity.track.*
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.DisplayableItem
import dev.olog.feature.presentation.base.model.presentationId

internal fun Folder.toTabDisplayableItem(
    resources: Resources,
    requestedSpanSize: Int
): DisplayableItem {
    return DisplayableAlbum(
        type = if (requestedSpanSize == 1) R.layout.item_tab_song else R.layout.item_tab_album,
        mediaId = presentationId,
        title = title,
        subtitle = DisplayableAlbum.readableSongCount(
            resources,
            size
        )
    )
}

internal fun Playlist.toAutoPlaylist(): DisplayableItem {
    val layoutId = if (isPodcast) R.layout.item_tab_podcast_auto_playlist else R.layout.item_tab_auto_playlist
    return DisplayableAlbum(
        type = layoutId,
        mediaId = presentationId,
        title = title,
        subtitle = ""
    )
}

internal fun Playlist.toTabDisplayableItem(
    resources: Resources,
    requestedSpanSize: Int
): DisplayableItem {
    val layoutId = if (requestedSpanSize == 1) {
        R.layout.item_tab_song // TODO check on podcast
    } else {
        if (isPodcast) R.layout.item_tab_podcast_playlist else R.layout.item_tab_album
    }

    return DisplayableAlbum(
        type = layoutId,
        mediaId = presentationId,
        title = title,
        subtitle = DisplayableAlbum.readableSongCount(
            resources,
            size
        )
    )
}

internal fun Genre.toTabDisplayableItem(
    resources: Resources,
    requestedSpanSize: Int
): DisplayableItem {
    return DisplayableAlbum(
        type = if (requestedSpanSize == 1) R.layout.item_tab_song else R.layout.item_tab_album,
        mediaId = presentationId,
        title = name,
        subtitle = DisplayableAlbum.readableSongCount(
            resources,
            size
        )
    )
}