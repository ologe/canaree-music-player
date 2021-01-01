package dev.olog.feature.detail.related.artist

import dev.olog.core.mediaid.MediaId

data class RelatedArtistFragmentModel(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
)