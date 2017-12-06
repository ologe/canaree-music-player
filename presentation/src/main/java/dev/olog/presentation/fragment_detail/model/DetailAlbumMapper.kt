package dev.olog.presentation.fragment_detail.model

import android.content.res.Resources
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Folder
import dev.olog.domain.entity.Genre
import dev.olog.domain.entity.Playlist
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper

fun Folder.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album_mini,
            MediaIdHelper.folderId(path),
            title.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase()
    )
}

fun Playlist.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album_mini,
            MediaIdHelper.playlistId(id),
            title.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase()
    )
}

fun Album.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album,
            MediaIdHelper.albumId(id),
            title,
            resources.getQuantityString(R.plurals.song_count, this.songs, this.songs).toLowerCase(),
            image
    )
}

fun Genre.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album_mini,
            MediaIdHelper.genreId(id),
            name.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase()
    )
}