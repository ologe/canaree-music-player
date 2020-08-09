package dev.olog.feature.library.album

import dev.olog.feature.presentation.base.model.PresentationId

data class AlbumFragmentItem(
    val mediaId: PresentationId.Category,
    val title: String,
    val subtitle: String
)