package dev.olog.presentation.tab.mapper

import android.content.res.Resources
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.tab.TabListItem
import kotlin.time.Duration.Companion.milliseconds

internal fun Folder.toTabDisplayableItem(
    resources: Resources,
    requestedSpanSize: Int
): TabListItem.Album {
    return TabListItem.Album.Scrollable(
        mediaId = getMediaId(),
        title = title,
        subtitle = DisplayableAlbum.readableSongCount(resources, size),
        asRow = requestedSpanSize == 1,
    )
}

internal fun Playlist.toAutoPlaylist(): TabListItem.Album {
    return TabListItem.Album.NonScrollable(
        mediaId = getMediaId(),
        title = title,
        subtitle = null,
    )
}

internal fun Playlist.toTabDisplayableItem(
    resources: Resources,
    requestedSpanSize: Int
): TabListItem.Album {
    return TabListItem.Album.Scrollable(
        mediaId = getMediaId(),
        title = title,
        subtitle = DisplayableAlbum.readableSongCount(resources, size),
        asRow = requestedSpanSize == 1,
    )
}

internal fun Song.toTabDisplayableItem(): TabListItem {
    if (isPodcast) {
        return TabListItem.Podcast(
            mediaId = getMediaId(),
            title = title,
            artist = artist,
            album = album,
            duration = duration.milliseconds,
        )
    }
    return TabListItem.Track(
        mediaId = getMediaId(),
        title = title,
        artist = artist,
        album = album,
    )
}


internal fun Album.toTabDisplayableItem(requestedSpanSize: Int): TabListItem.Album {
    return TabListItem.Album.Scrollable(
        mediaId = getMediaId(),
        title = title,
        subtitle = artist,
        asRow = requestedSpanSize == 1,
    )
}

internal fun Artist.toTabDisplayableItem(
    resources: Resources,
    requestedSpanSize: Int
): TabListItem.Album {
    return TabListItem.Album.Scrollable(
        mediaId = getMediaId(),
        title = name,
        subtitle = DisplayableAlbum.readableSongCount(resources, songs),
        asRow = requestedSpanSize == 1,
    )
}


internal fun Genre.toTabDisplayableItem(
    resources: Resources,
    requestedSpanSize: Int
): TabListItem.Album {
    return TabListItem.Album.Scrollable(
        mediaId = getMediaId(),
        title = name,
        subtitle = DisplayableAlbum.readableSongCount(resources, size),
        asRow = requestedSpanSize == 1,
    )
}

internal fun Album.toTabLastPlayedDisplayableItem(): TabListItem.Album {
    return TabListItem.Album.NonScrollable(
        mediaId = getMediaId(),
        title = title,
        subtitle = artist,
    )
}

internal fun Artist.toTabLastPlayedDisplayableItem(resources: Resources): TabListItem.Album {
    return TabListItem.Album.NonScrollable(
        mediaId = getMediaId(),
        title = name,
        subtitle = DisplayableAlbum.readableSongCount(resources, songs),
    )
}