package dev.olog.presentation.dialog_entry

import android.content.Context
import dev.olog.domain.entity.*
import dev.olog.presentation.R
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.TextUtils

fun Folder.toDialogItem(): DialogModel {
    return DialogModel(
            R.layout.item_dialog_image,
            MediaIdHelper.folderId(this.path),
            title.capitalize(),
            null,
            image,
            false,
            false,
            null
    )
}

fun Playlist.toDialogItem(): DialogModel {
    return DialogModel(
            R.layout.item_dialog_image,
            MediaIdHelper.playlistId(this.id),
            title.capitalize(),
            null,
            image,
            false,
            false,
            null
    )
}

fun Song.toDialogItem(context: Context): DialogModel {
    return DialogModel(
            R.layout.item_dialog_image,
            MediaIdHelper.songId(this.id),
            title,
            "$artist${TextUtils.MIDDLE_DOT_SPACED}$album",
            image,
            this.artist != context.getString(R.string.unknown_artist),
            this.album != context.getString(R.string.unknown_album),
            null
    )
}

fun Album.toDialogItem(): DialogModel {
    return DialogModel(
            R.layout.item_dialog_image,
            MediaIdHelper.albumId(this.id),
            title,
            artist,
            image,
            false,
            false,
            null
    )
}

fun Artist.toDialogItem(): DialogModel {
    return DialogModel(
            R.layout.item_dialog_image,
            MediaIdHelper.artistId(this.id),
            name,
            null,
            image,
            false,
            false,
            null
    )
}

fun Genre.toDialogItem(): DialogModel {
    return DialogModel(
            R.layout.item_dialog_image,
            MediaIdHelper.genreId(this.id),
            name,
            null,
            image,
            false,
            false,
            null
    )
}