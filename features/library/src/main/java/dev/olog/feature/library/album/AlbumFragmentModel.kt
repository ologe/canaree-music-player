package dev.olog.feature.library.album

import dev.olog.feature.presentation.base.model.PresentationId
import javax.annotation.concurrent.Immutable

@Immutable
data class AlbumFragmentModel(
    val mediaId: PresentationId.Category,
    val title: String,
    val subtitle: String
)