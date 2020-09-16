package dev.olog.feature.library.track

import dev.olog.feature.presentation.base.model.PresentationId
import javax.annotation.concurrent.Immutable

@Immutable
internal sealed class TracksFragmentModel {

    @Immutable
    object Shuffle: TracksFragmentModel()

    @Immutable
    data class Track(
        val mediaId: PresentationId.Track,
        val title: String,
        val subtitle: String
    ): TracksFragmentModel()

    @Immutable
    data class Podcast(
        val mediaId: PresentationId.Track,
        val title: String,
        val subtitle: String
    ): TracksFragmentModel()

}