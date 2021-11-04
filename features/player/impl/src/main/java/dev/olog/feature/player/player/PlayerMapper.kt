package dev.olog.feature.player.player

import dev.olog.media.model.PlayerItem
import dev.olog.feature.base.model.DisplayableItem
import dev.olog.feature.base.model.DisplayableTrack
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