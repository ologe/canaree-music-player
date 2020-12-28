package dev.olog.presentation.createplaylist.mapper

import dev.olog.core.entity.track.Track
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableTrack

internal fun Track.toDisplayableItem(): DisplayableTrack {
    return DisplayableTrack(
        type = R.layout.item_create_playlist,
        mediaId = getMediaId(),
        title = this.title,
        artist = this.artist,
        album = this.album,
        idInPlaylist = -1,
        dataModified = this.dateModified
    )
}