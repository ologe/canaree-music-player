package dev.olog.presentation.tab.mapper

import android.content.res.Resources
import dev.olog.domain.entity.track.*
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.feature.presentation.base.model.presentationId
import java.util.concurrent.TimeUnit

internal fun Folder.toTabDisplayableItem(
    resources: Resources,
    requestedSpanSize: Int
): DisplayableItem {
    return DisplayableAlbum(
        type = if (requestedSpanSize == 1) R.layout.item_tab_song else R.layout.item_tab_album,
        mediaId = presentationId,
        title = title,
        subtitle = DisplayableAlbum.readableSongCount(resources, size)
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
        subtitle = DisplayableAlbum.readableSongCount(resources, size)
    )
}

internal fun Song.toTabDisplayableItem(): DisplayableItem {
    return DisplayableTrack(
        type = if (isPodcast) R.layout.item_tab_podcast else R.layout.item_tab_song,
        mediaId = presentationId,
        title = title,
        artist = artist,
        album = album,
        idInPlaylist = if (isPodcast) TimeUnit.MILLISECONDS.toMinutes(duration).toInt() else this.idInPlaylist,
        dataModified = this.dateModified,
        duration = this.duration
    )
}


internal fun Album.toTabDisplayableItem(requestedSpanSize: Int): DisplayableItem {
    return DisplayableAlbum(
        type = if (requestedSpanSize == 1) R.layout.item_tab_song else R.layout.item_tab_album,
        mediaId = presentationId,
        title = title,
        subtitle = artist
    )
}

internal fun Artist.toTabDisplayableItem(
    resources: Resources,
    requestedSpanSize: Int
): DisplayableItem {
    val songs = DisplayableAlbum.readableSongCount(resources, songs)

    return DisplayableAlbum(
        type = if (requestedSpanSize == 1) R.layout.item_tab_song else R.layout.item_tab_artist,
        mediaId = presentationId,
        title = name,
        subtitle = songs
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
        subtitle = DisplayableAlbum.readableSongCount(resources, size)
    )
}

internal fun Album.toTabLastPlayedDisplayableItem(): DisplayableItem {
    return DisplayableAlbum(
        type = R.layout.item_tab_album_last_played,
        mediaId = presentationId,
        title = title,
        subtitle = artist
    )
}

internal fun Artist.toTabLastPlayedDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableAlbum(
        type = R.layout.item_tab_artist_last_played,
        mediaId = presentationId,
        title = name,
        subtitle = DisplayableAlbum.readableSongCount(resources, songs)
    )
}