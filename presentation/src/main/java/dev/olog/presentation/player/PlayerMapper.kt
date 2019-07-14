package dev.olog.presentation.player

import dev.olog.media.model.PlayerItem
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem2
import dev.olog.presentation.model.DisplayableTrack

internal fun PlayerItem.toDisplayableItem(): DisplayableItem2 {
    return DisplayableTrack(
        type = R.layout.item_mini_queue,
        mediaId = mediaId,
        title = title,
        artist = artist,
        album = "",
        idInPlaylist = idInPlaylist.toInt()
    )
}