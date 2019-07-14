package dev.olog.presentation.tab.mapper

import android.content.res.Resources
import dev.olog.core.entity.track.*
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableItem2
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.shared.utils.TextUtils

internal fun Folder.toTabDisplayableItem(resources: Resources): DisplayableItem2 {
    return DisplayableAlbum(
        type = R.layout.item_tab_album,
        mediaId = getMediaId(),
        title = title,
        subtitle = DisplayableItem.handleSongListSize(resources, size)
    )
}

internal fun Playlist.toAutoPlaylist(): DisplayableItem2 {
    return DisplayableAlbum(
        type = R.layout.item_tab_auto_playlist,
        mediaId = getMediaId(),
        title = title,
        subtitle = ""
    )
}

internal fun Playlist.toTabDisplayableItem(resources: Resources): DisplayableItem2 {

    return DisplayableAlbum(
        type = R.layout.item_tab_album,
        mediaId = getMediaId(),
        title = title,
        subtitle = DisplayableItem.handleSongListSize(resources, size)
    )
}

internal fun Song.toTabDisplayableItem(): DisplayableItem2 {
    return DisplayableTrack(
        type = R.layout.item_tab_song,
        mediaId = getMediaId(),
        title = title,
        artist = artist,
        album = album,
        idInPlaylist = this.idInPlaylist
    )
}


internal fun Album.toTabDisplayableItem(): DisplayableItem2 {
    return DisplayableAlbum(
        type = R.layout.item_tab_album,
        mediaId = getMediaId(),
        title = title,
        subtitle = artist
    )
}

internal fun Artist.toTabDisplayableItem(resources: Resources): DisplayableItem2 {
    val songs = DisplayableItem.handleSongListSize(resources, songs)

    return DisplayableAlbum(
        type = R.layout.item_tab_artist,
        mediaId = getMediaId(),
        title = name,
        subtitle = songs
    )
}


internal fun Genre.toTabDisplayableItem(resources: Resources): DisplayableItem2 {
    return DisplayableAlbum(
        type = R.layout.item_tab_album,
        mediaId = getMediaId(),
        title = name,
        subtitle = DisplayableItem.handleSongListSize(resources, size)
    )
}

internal fun Album.toTabLastPlayedDisplayableItem(): DisplayableItem2 {
    return DisplayableAlbum(
        type = R.layout.item_tab_album_last_played,
        mediaId = getMediaId(),
        title = title,
        subtitle = artist
    )
}

internal fun Artist.toTabLastPlayedDisplayableItem(resources: Resources): DisplayableItem2 {
    return DisplayableAlbum(
        type = R.layout.item_tab_artist_last_played,
        mediaId = getMediaId(),
        title = name,
        subtitle = DisplayableItem.handleSongListSize(resources, songs)
    )
}