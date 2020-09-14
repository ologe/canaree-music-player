package dev.olog.feature.library.track

import dev.olog.domain.entity.track.Song
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.toPresentation
import dev.olog.shared.TextUtils

internal sealed class TracksFragmentModel {

    object Shuffle: TracksFragmentModel()

    data class Track(
        val mediaId: PresentationId.Track,
        val title: String,
        val subtitle: String
    ): TracksFragmentModel()

    data class Podcast(
        val mediaId: PresentationId.Track,
        val title: String,
        val subtitle: String
    ): TracksFragmentModel()

}

internal fun Song.toPresentation(): TracksFragmentModel {
    return TracksFragmentModel.Track(
        mediaId = this.mediaId.toPresentation(),
        title = this.title,
        subtitle = TextUtils.buildSubtitle(this.artist, this.album)
    )
}