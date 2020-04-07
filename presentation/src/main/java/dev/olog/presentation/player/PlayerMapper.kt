package dev.olog.presentation.player

import dev.olog.lib.media.model.PlayerItem
import dev.olog.presentation.PresentationId
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.toPresentation

internal fun PlayerItem.toDisplayableItem(): DisplayableItem {
    return DisplayableTrack(
        type = R.layout.item_mini_queue,
        mediaId = mediaId.toPresentation() as PresentationId.Track, // TODO enforce
        title = title,
        artist = artist,
        album = "",
        idInPlaylist = idInPlaylist.toInt(),
        dataModified = -1,
        duration = 0 // TODO ??
    )
}