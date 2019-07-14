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
        R.layout.item_tab_album,
        getMediaId(),
        title,
        DisplayableItem.handleSongListSize(resources, size)
    )
}

internal fun Playlist.toAutoPlaylist(): DisplayableItem2 {
    return DisplayableAlbum(
        R.layout.item_tab_auto_playlist,
        getMediaId(),
        title,
        ""
    )
}

internal fun Playlist.toTabDisplayableItem(resources: Resources): DisplayableItem2 {

    val size = DisplayableItem.handleSongListSize(resources, size)

    return DisplayableAlbum(
        R.layout.item_tab_album,
        getMediaId(),
        title,
        size
    )
}

internal fun Song.toTabDisplayableItem(): DisplayableItem2 {
    return DisplayableTrack(
        R.layout.item_tab_song,
        getMediaId(),
        title,
        artist,
        album,
        this.idInPlaylist
    )
}


internal fun Album.toTabDisplayableItem(): DisplayableItem2 {
    return DisplayableAlbum(
        R.layout.item_tab_album,
        getMediaId(),
        title,
        artist
    )
}

internal fun Artist.toTabDisplayableItem(resources: Resources): DisplayableItem2 {
    val songs = DisplayableItem.handleSongListSize(resources, songs)

    return DisplayableAlbum(
        R.layout.item_tab_artist,
        getMediaId(),
        name,
        songs
    )
}


internal fun Genre.toTabDisplayableItem(resources: Resources): DisplayableItem2 {
    return DisplayableAlbum(
        R.layout.item_tab_album,
        getMediaId(),
        name,
        DisplayableItem.handleSongListSize(resources, size)
    )
}

internal fun Album.toTabLastPlayedDisplayableItem(): DisplayableItem2 {
    return DisplayableAlbum(
        R.layout.item_tab_album_last_played,
        getMediaId(),
        title,
        artist
    )
}

internal fun Artist.toTabLastPlayedDisplayableItem(resources: Resources): DisplayableItem2 {
    val songs = DisplayableItem.handleSongListSize(resources, songs)

    return DisplayableAlbum(
        R.layout.item_tab_artist_last_played,
        getMediaId(),
        name,
        songs
    )
}