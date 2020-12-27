package dev.olog.feature.detail.related.artist

import dev.olog.core.MediaId

data class RelatedArtistFragmentModel(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
)