package dev.olog.presentation.model

import android.content.res.Resources
import dev.olog.domain.entity.*
import dev.olog.presentation.R
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.TextUtils

fun Folder.toDisplayableItem(resources: Resources): DisplayableItem{
    return DisplayableItem(
            R.layout.item_tab_album,
            MediaIdHelper.folderId(path),
            title.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase()
    )
}

fun Playlist.toDisplayableItem(resources: Resources): DisplayableItem{
    val listSize = if (this.size == -1){ "" } else {
        resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase()
    }

    return DisplayableItem(
            R.layout.item_tab_album,
            MediaIdHelper.playlistId(id),
            title.capitalize(),
            listSize
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

fun Artist.toDisplayableItem(resources: Resources): DisplayableItem{
    val songs = resources.getQuantityString(R.plurals.song_count, this.songs, this.songs)
    val albums = if (this.albums == 0) "" else {
        "${resources.getQuantityString(R.plurals.album_count, this.albums, this.albums)}${TextUtils.MIDDLE_DOT_SPACED}"
    }

    return DisplayableItem(
            R.layout.item_tab_album,
            MediaIdHelper.artistId(id),
            name,
            "$albums$songs".toLowerCase()
    )
}

fun Genre.toDisplayableItem(resources: Resources): DisplayableItem{
    return DisplayableItem(
            R.layout.item_tab_album,
            MediaIdHelper.genreId(id),
            name,
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase()
    )
}