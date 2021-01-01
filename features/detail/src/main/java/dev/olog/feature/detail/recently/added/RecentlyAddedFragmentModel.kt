package dev.olog.feature.detail.recently.added

import dev.olog.domain.mediaid.MediaId

data class RecentlyAddedFragmentModel(
    val mediaId: MediaId.Track,
    val title: String,
    val subtitle: String,
)