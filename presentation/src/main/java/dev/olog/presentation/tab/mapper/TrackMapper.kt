package dev.olog.presentation.tab.mapper

import android.content.res.Resources
import dev.olog.core.MediaId
import dev.olog.core.entity.track.*
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.TextUtils

internal fun Folder.toTabDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
        R.layout.item_tab_album,
        MediaId.folderId(path),
        title,
        DisplayableItem.handleSongListSize(resources, size)
    )
}

internal fun Playlist.toAutoPlaylist(): DisplayableItem {

    return DisplayableItem(
        R.layout.item_tab_auto_playlist,
        MediaId.playlistId(id),
        title,
        ""
    )
}

internal fun Playlist.toTabDisplayableItem(resources: Resources): DisplayableItem {

    val size = DisplayableItem.handleSongListSize(resources, size)

    return DisplayableItem(
        R.layout.item_tab_album,
        MediaId.playlistId(id),
        title,
        size
    )
}

internal fun Song.toTabDisplayableItem(): DisplayableItem {
    val artist = DisplayableItem.adjustArtist(this.artist)
    val album = DisplayableItem.adjustAlbum(this.album)

    return DisplayableItem(
        R.layout.item_tab_song,
        MediaId.songId(this.id),
        title,
        "$artist${TextUtils.MIDDLE_DOT_SPACED}$album",
        true
    )
}



internal fun Album.toTabDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_tab_album,
        MediaId.albumId(id),
        title,
        DisplayableItem.adjustArtist(artist)
    )
}

internal fun Artist.toTabDisplayableItem(resources: Resources): DisplayableItem {
    val songs = DisplayableItem.handleSongListSize(resources, songs)
    var albums = DisplayableItem.handleAlbumListSize(resources, albums)
    if (albums.isNotBlank()) albums+= TextUtils.MIDDLE_DOT_SPACED

    return DisplayableItem(
        R.layout.item_tab_artist,
        MediaId.artistId(id),
        name,
        albums + songs
    )
}


internal fun Genre.toTabDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
        R.layout.item_tab_album,
        MediaId.genreId(id),
        name,
        DisplayableItem.handleSongListSize(resources, size)
    )
}

internal fun Album.toTabLastPlayedDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_tab_album_last_played,
        MediaId.albumId(id),
        title,
        DisplayableItem.adjustArtist(artist)
    )
}

internal fun Artist.toTabLastPlayedDisplayableItem(resources: Resources): DisplayableItem {
    val songs = DisplayableItem.handleSongListSize(resources, songs)
    var albums = DisplayableItem.handleAlbumListSize(resources, albums)
    if (albums.isNotBlank()) albums+= TextUtils.MIDDLE_DOT_SPACED

    return DisplayableItem(
        R.layout.item_tab_artist_last_played,
        MediaId.artistId(id),
        name,
        albums + songs
    )
}