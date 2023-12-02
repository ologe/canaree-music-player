package dev.olog.presentation.createplaylist.mapper

import dev.olog.core.entity.track.Song

internal fun Song.toDisplayableItem(): CreatePlaylistFragmentItem {
    return CreatePlaylistFragmentItem(
        mediaId = getMediaId(),
        title = this.title,
        artist = artist,
        album = album,
        isChecked = false,
    )
}