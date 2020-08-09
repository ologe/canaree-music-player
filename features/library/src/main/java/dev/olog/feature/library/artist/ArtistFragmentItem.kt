package dev.olog.feature.library.artist

import dev.olog.feature.presentation.base.model.PresentationId

data class ArtistFragmentItem(
    val mediaId: PresentationId.Category,
    val title: String,
    val subtitle: String
)