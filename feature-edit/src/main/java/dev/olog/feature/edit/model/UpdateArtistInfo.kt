package dev.olog.feature.edit.model

import dev.olog.feature.presentation.base.model.PresentationId

data class UpdateArtistInfo(
    val mediaId: PresentationId.Category,
    val name: String,
    val albumArtist: String,
    val isPodcast: Boolean
)