package dev.olog.feature.detail.recently.added

import dev.olog.core.MediaId

data class RecentlyAddedFragmentModel(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
)