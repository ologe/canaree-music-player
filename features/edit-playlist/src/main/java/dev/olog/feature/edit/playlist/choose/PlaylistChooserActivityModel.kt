package dev.olog.feature.edit.playlist.choose

import android.content.res.Resources
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Playlist
import dev.olog.shared.android.DisplayableItemUtils

data class PlaylistChooserActivityModel(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
)

internal fun Playlist.toDisplayableItem(resources: Resources): PlaylistChooserActivityModel {
    return PlaylistChooserActivityModel(
        mediaId = getMediaId(),
        title = title,
        subtitle = DisplayableItemUtils.readableSongCount(resources, size)
    )
}