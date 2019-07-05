package dev.olog.presentation.player

import dev.olog.media.model.PlayerItem
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem

internal fun PlayerItem.toDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_mini_queue,
        mediaId,
        title,
        artist,
        isPlayable = true,
        trackNumber = "$idInPlaylist"
    )
}