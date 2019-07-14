package dev.olog.presentation.createplaylist.mapper

import dev.olog.core.entity.track.Song
import dev.olog.core.entity.track.getMediaId
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableTrack

internal fun Song.toDisplayableItem(): DisplayableTrack {
    return DisplayableTrack(
        R.layout.item_choose_track,
        getMediaId(),
        this.title,
        this.artist,
        this.album,
        this.idInPlaylist
    )
}