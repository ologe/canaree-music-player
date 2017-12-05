package dev.olog.presentation.fragment_detail.model

import android.content.Context
import android.content.res.Resources
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Folder
import dev.olog.domain.entity.Genre
import dev.olog.domain.entity.Playlist
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper

fun Folder.toDetailDisplayableItem(context: Context): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album_mini,
            MediaIdHelper.folderId(path),
            title.capitalize(),
            context.resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase()
    )
}

fun Playlist.toDetailDisplayableItem(resources: Resources, playlistSize: Int): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album_mini,
            MediaIdHelper.playlistId(id),
            title.capitalize(),
            resources.getQuantityString(R.plurals.song_count, playlistSize, playlistSize).toLowerCase()
    )
}

fun Album.toDetailDisplayableItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album,
            MediaIdHelper.albumId(id),
            title,
            artist,
            image
    )
}

fun Genre.toDetailDisplayableItem(resources: Resources, genreSize: Int): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album_mini,
            MediaIdHelper.genreId(id),
            name.capitalize(),
            resources.getQuantityString(R.plurals.song_count, genreSize, genreSize).toLowerCase()
    )
}