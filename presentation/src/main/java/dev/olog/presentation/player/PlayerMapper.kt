package dev.olog.presentation.player

import dev.olog.feature.media.model.PlayerItem
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.R

internal fun PlayerItem.toDisplayableItem(): DisplayableItem {
    return DisplayableTrack(
        type = R.layout.item_mini_queue,
        mediaId = mediaId,
        title = title,
        artist = artist,
        album = "",
        idInPlaylist = idInPlaylist.toInt(),
        dataModified = -1
    )
}