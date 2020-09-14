package dev.olog.feature.library.artist

import dev.olog.feature.presentation.base.model.PresentationId

data class ArtistFragmentModel(
    val mediaId: PresentationId.Category,
    val title: String,
    val subtitle: String,
)