package dev.olog.feature.edit.model

import dev.olog.feature.presentation.base.model.PresentationId

data class UpdateAlbumInfo(
    val mediaId: PresentationId.Category,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val genre: String,
    val year: String
)