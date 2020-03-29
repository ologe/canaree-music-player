package dev.olog.presentation.detail.mapper

import android.content.res.Resources
import dev.olog.core.entity.spotify.SpotifyAlbum
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableAlbum
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