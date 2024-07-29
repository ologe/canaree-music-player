package dev.olog.presentation.createplaylist.mapper

import dev.olog.core.entity.track.Song
import dev.olog.presentation.createplaylist.CreatePlaylistItem
import dev.olog.shared.android.TextUtils

internal fun Song.toDisplayableItem(): CreatePlaylistItem {
    return CreatePlaylistItem(
//        type = R.layout.item_create_playlist,
        mediaId = getMediaId(),
        title = this.title,
        subtitle = TextUtils.subtitle(artist, album),
        isChecked = false,
//        idInPlaylist = this.idInPlaylist,
//        dataModified = this.dateModified
    )
}