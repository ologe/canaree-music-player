package dev.olog.feature.edit.playlist.create

import dev.olog.domain.entity.track.Track
import dev.olog.domain.mediaid.MediaId
import dev.olog.shared.android.DisplayableItemUtils

internal data class CreatePlaylistFragmentModel(
    val mediaId: MediaId.Track,
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