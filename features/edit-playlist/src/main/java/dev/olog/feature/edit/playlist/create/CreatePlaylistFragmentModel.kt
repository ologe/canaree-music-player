package dev.olog.feature.edit.playlist.create

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Track
import dev.olog.shared.android.DisplayableItemUtils

internal data class CreatePlaylistFragmentModel(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
)

internal fun Track.toDisplayableItem(): CreatePlaylistFragmentModel {
    return CreatePlaylistFragmentModel(
        mediaId = getMediaId(),
        title = this.title,
        subtitle = DisplayableItemUtils.trackSubtitle(artist, album),
    )
}