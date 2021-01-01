package dev.olog.feature.detail.related.artist

import dev.olog.domain.mediaid.MediaId

data class RelatedArtistFragmentModel(
    val mediaId: MediaId.Category,
    val title: String,
    val subtitle: String,
)