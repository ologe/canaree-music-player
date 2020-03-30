package dev.olog.presentation.detail.mapper

import android.content.res.Resources
import dev.olog.core.entity.spotify.SpotifyAlbum
import dev.olog.core.entity.spotify.SpotifyTrack
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.toPresentation

internal fun SpotifyAlbum.toDetailDisplayableItem(resources: Resources): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_detail_album_spotify,
        mediaId = mediaId.toPresentation(),
        title = title,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.songs,
            this.songs
        ).toLowerCase()
    )
}

internal fun SpotifyTrack.toDetailDisplayableItem(): DisplayableTrack {
    return DisplayableTrack(
        type = R.layout.item_detail_song_with_track_spotify,
        mediaId = mediaId.toPresentation(),
        title = name,
        artist = artist,
        album = album,
        idInPlaylist = trackNumber,
        dataModified = -1,
        duration = this.duration,
        isExplicit = this.isExplicit
    )
}