package dev.olog.feature.playlist.create

import dev.olog.core.entity.track.Song
import dev.olog.feature.base.model.DisplayableTrack
import dev.olog.feature.playlist.R

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