package dev.olog.feature.player

import dev.olog.lib.media.model.PlayerItem
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.DisplayableItem
import dev.olog.feature.presentation.base.model.DisplayableTrack
import dev.olog.feature.presentation.base.model.toPresentation

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