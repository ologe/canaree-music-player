package dev.olog.presentation.tab.mapper

import android.content.res.Resources
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.presentation.tab.adapter.TabItem
import dev.olog.shared.android.TextUtils
import kotlin.time.Duration.Companion.milliseconds

internal fun Folder.toTabDisplayableItem(
    resources: Resources,
    requestedSpanSize: Int
): TabItem.Album {
    return TabItem.Album(
        asRow = requestedSpanSize == 1,
        mediaId = getMediaId(),
        title = title,
        subtitle = TextUtils.readableSongCount(resources, size)
    )
}

internal fun Playlist.toAutoPlaylist(): TabItem.Album {
    return TabItem.Album(
        asRow = false,
        mediaId = getMediaId(),
        title = title,
        subtitle = null,
    )
}

internal fun Playlist.toTabDisplayableItem(
    resources: Resources,
    requestedSpanSize: Int
): TabItem.Album {
    return TabItem.Album(
        asRow = requestedSpanSize == 1,
        mediaId = getMediaId(),
        title = title,
        subtitle = TextUtils.readableSongCount(resources, size)
    )
}

internal fun Song.toTabDisplayableItem(): TabItem {
    if (isPodcast) {
        return TabItem.Podcast(
            mediaId = getMediaId(),
            title = title,
            subtitle = TextUtils.subtitle(artist, album),
            duration = "${duration.milliseconds.inWholeSeconds}m",
        )
    }
    return TabItem.Song(
        mediaId = getMediaId(),
        title = title,
        artist = artist,
        album = album
    )
}


internal fun Album.toTabDisplayableItem(requestedSpanSize: Int): TabItem.Album {
    return TabItem.Album(
        asRow = requestedSpanSize == 1,
        mediaId = getMediaId(),
        title = title,
        subtitle = artist
    )
}

internal fun Artist.toTabDisplayableItem(
    resources: Resources,
    requestedSpanSize: Int
): TabItem.Album {
    val songs = TextUtils.readableSongCount(resources, songs)

    return TabItem.Album(
        asRow = requestedSpanSize == 1,
        mediaId = getMediaId(),
        title = name,
        subtitle = songs
    )
}


internal fun Genre.toTabDisplayableItem(
    resources: Resources,
    requestedSpanSize: Int
): TabItem.Album {
    return TabItem.Album(
        asRow = requestedSpanSize == 1,
        mediaId = getMediaId(),
        title = name,
        subtitle = TextUtils.readableSongCount(resources, size)
    )
}

internal fun Album.toTabLastPlayedDisplayableItem(): TabItem.Album {
    return TabItem.Album(
        asRow = false,
        mediaId = getMediaId(),
        title = title,
        subtitle = artist
    )
}

internal fun Artist.toTabLastPlayedDisplayableItem(resources: Resources): TabItem.Album {
    return TabItem.Album(
        asRow = false,
        mediaId = getMediaId(),
        title = name,
        subtitle = TextUtils.readableSongCount(resources, songs)
    )
}