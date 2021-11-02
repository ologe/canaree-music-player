package dev.olog.presentation.player

import dev.olog.media.model.PlayerItem
import dev.olog.presentation.R
import dev.olog.feature.base.DisplayableItem
import dev.olog.feature.base.DisplayableTrack

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