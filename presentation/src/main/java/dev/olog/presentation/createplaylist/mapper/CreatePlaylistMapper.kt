package dev.olog.presentation.createplaylist.mapper

import dev.olog.core.entity.track.Song
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.PlaylistTrack
import dev.olog.presentation.model.getMediaId

internal fun PlaylistTrack.toDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_choose_track,
        getMediaId(),
        this.title,
        DisplayableItem.adjustArtist(this.artist),
        true
    )
}

internal fun Song.toPlaylistTrack(): PlaylistTrack {
    return PlaylistTrack(
        this.id,
        this.artistId,
        this.albumId,
        this.title,
        this.artist,
        this.albumArtist,
        this.album,
        this.duration,
        this.dateAdded,
        this.path,
        this.folder,
        this.discNumber,
        this.trackNumber,
        false
    )
}