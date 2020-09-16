package dev.olog.feature.library.artist

import dev.olog.feature.presentation.base.model.PresentationId
import javax.annotation.concurrent.Immutable

@Immutable
data class ArtistFragmentModel(
    val mediaId: PresentationId.Category,
    val title: String,
    val subtitle: String,
)