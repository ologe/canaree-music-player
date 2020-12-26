package dev.olog.presentation.player

import dev.olog.lib.media.model.PlayerItem
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack

internal fun PlayerItem.toDisplayableItem(): DisplayableItem {
    return DisplayableTrack(
        type = R.layout.item_mini_queue,
        mediaId = mediaId,
        title = title,
        artist = artist,
        album = "",
        idInPlaylist = progressive.toInt(),
        dataModified = -1
    )
}