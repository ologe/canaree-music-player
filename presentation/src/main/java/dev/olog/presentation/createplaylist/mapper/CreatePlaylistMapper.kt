package dev.olog.presentation.createplaylist.mapper

import dev.olog.core.entity.track.Song
import dev.olog.presentation.R
import dev.olog.ui.model.DisplayableTrack

internal fun Song.toDisplayableItem(): DisplayableTrack {
    return DisplayableTrack(
        type = R.layout.item_create_playlist,
        mediaId = getMediaId(),
        title = this.title,
        artist = this.artist,
        album = this.album,
        idInPlaylist = this.idInPlaylist,
        dataModified = this.dateModified
    )
}