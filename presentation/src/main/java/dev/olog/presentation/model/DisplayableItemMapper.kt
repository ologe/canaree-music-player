package dev.olog.presentation.model

import android.content.Context
import dev.olog.domain.entity.*
import dev.olog.presentation.R
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.TextUtils

fun Folder.toDisplayableItem(context: Context): DisplayableItem{
    return DisplayableItem(
            R.layout.item_tab_album,
            MediaIdHelper.folderId(path),
            title.capitalize(),
            context.resources.getQuantityString(R.plurals.song_count,
                    this.size, this.size).toLowerCase()
    )
}

fun Playlist.toDisplayableItem(): DisplayableItem{
    return dev.olog.presentation.model.DisplayableItem(
            R.layout.item_tab_album_alt,
            MediaIdHelper.playlistId(id),
            title.capitalize()
    )
}

fun Song.toDisplayableItem(): DisplayableItem{
    return DisplayableItem(
            R.layout.item_tab_song,
            MediaIdHelper.songId(id),
            title,
            "$artist${TextUtils.MIDDLE_DOT_SPACED}$album",
            image,
            true,
            isRemix,
            isExplicit
    )
}

fun Album.toDisplayableItem(): DisplayableItem{
    return DisplayableItem(
            R.layout.item_tab_album,
            MediaIdHelper.albumId(id),
            title,
            artist,
            image
    )
}

fun Artist.toDisplayableItem(context: Context): DisplayableItem{
    val songs = context.resources.getQuantityString(R.plurals.song_count, this.songs, this.songs)
    val albums = context.resources.getQuantityString(R.plurals.album_count, this.albums, this.albums)

    return DisplayableItem(
            R.layout.item_tab_album,
            MediaIdHelper.artistId(id),
            name,
            "$albums, $songs".toLowerCase()
    )
}

fun Genre.toDisplayableItem(): DisplayableItem{
    return DisplayableItem(
            R.layout.item_tab_album_alt,
            MediaIdHelper.genreId(id),
            name
    )
}