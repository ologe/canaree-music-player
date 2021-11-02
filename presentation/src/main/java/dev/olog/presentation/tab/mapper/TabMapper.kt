package dev.olog.presentation.tab.mapper

import android.content.res.Resources
import dev.olog.core.entity.track.*
import dev.olog.presentation.R
import dev.olog.feature.base.DisplayableAlbum
import dev.olog.feature.base.DisplayableItem
import dev.olog.feature.base.DisplayableTrack
import java.util.concurrent.TimeUnit

fun Folder.toTabDisplayableItem(
    resources: Resources,
    requestedSpanSize: Int
): DisplayableItem {
    return DisplayableAlbum(
        type = if (requestedSpanSize == 1) R.layout.item_tab_song else R.layout.item_tab_album,
        mediaId = getMediaId(),
        title = title,
        subtitle = DisplayableAlbum.readableSongCount(resources, size)
    )
}

fun Playlist.toAutoPlaylist(): DisplayableItem {
    return DisplayableAlbum(
        type = R.layout.item_tab_auto_playlist,
        mediaId = getMediaId(),
        title = title,
        subtitle = ""
    )
}

fun Playlist.toTabDisplayableItem(
    resources: Resources,
    requestedSpanSize: Int
): DisplayableItem {

    return DisplayableAlbum(
        type = if (requestedSpanSize == 1) R.layout.item_tab_song else R.layout.item_tab_album,
        mediaId = getMediaId(),
        title = title,
        subtitle = DisplayableAlbum.readableSongCount(resources, size)
    )
}

fun Song.toTabDisplayableItem(): DisplayableItem {
    return DisplayableTrack(
        type = if (isPodcast) R.layout.item_tab_podcast else R.layout.item_tab_song,
        mediaId = getMediaId(),
        title = title,
        artist = artist,
        album = album,
        idInPlaylist = if (isPodcast) TimeUnit.MILLISECONDS.toMinutes(duration).toInt() else this.idInPlaylist,
        dataModified = this.dateModified
    )
}


fun Album.toTabDisplayableItem(requestedSpanSize: Int): DisplayableItem {
    return DisplayableAlbum(
        type = if (requestedSpanSize == 1) R.layout.item_tab_song else R.layout.item_tab_album,
        mediaId = getMediaId(),
        title = title,
        subtitle = artist
    )
}

fun Artist.toTabDisplayableItem(
    resources: Resources,
    requestedSpanSize: Int
): DisplayableItem {
    val songs = DisplayableAlbum.readableSongCount(resources, songs)

    return DisplayableAlbum(
        type = if (requestedSpanSize == 1) R.layout.item_tab_song else R.layout.item_tab_artist,
        mediaId = getMediaId(),
        title = name,
        subtitle = songs
    )
}


fun Genre.toTabDisplayableItem(
    resources: Resources,
    requestedSpanSize: Int
): DisplayableItem {
    return DisplayableAlbum(
        type = if (requestedSpanSize == 1) R.layout.item_tab_song else R.layout.item_tab_album,
        mediaId = getMediaId(),
        title = name,
        subtitle = DisplayableAlbum.readableSongCount(resources, size)
    )
}

fun Album.toTabLastPlayedDisplayableItem(): DisplayableItem {
    return DisplayableAlbum(
        type = R.layout.item_tab_album_last_played,
        mediaId = getMediaId(),
        title = title,
        subtitle = artist
    )
}

fun Artist.toTabLastPlayedDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableAlbum(
        type = R.layout.item_tab_artist_last_played,
        mediaId = getMediaId(),
        title = name,
        subtitle = DisplayableAlbum.readableSongCount(resources, songs)
    )
}