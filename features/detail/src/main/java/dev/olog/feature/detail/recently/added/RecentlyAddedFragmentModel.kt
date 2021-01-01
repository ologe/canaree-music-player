package dev.olog.feature.detail.recently.added

import dev.olog.core.mediaid.MediaId

data class RecentlyAddedFragmentModel(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
)