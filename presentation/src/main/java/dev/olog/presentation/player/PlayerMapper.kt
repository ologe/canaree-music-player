package dev.olog.presentation.player

import dev.olog.media.model.PlayerItem

internal fun PlayerItem.toDisplayableItem(): dev.olog.presentation.player.PlayerItem {
    return dev.olog.presentation.player.PlayerItem.Track(
        mediaId = mediaId,
        title = title,
        subtitle = artist,
        idInPlaylist = idInPlaylist.toInt(),
    )
}