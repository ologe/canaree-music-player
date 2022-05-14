package dev.olog.feature.player.main

import dev.olog.feature.media.api.model.PlayerItem
import dev.olog.ui.model.DisplayableItem
import dev.olog.ui.model.DisplayableTrack
import dev.olog.feature.player.R

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