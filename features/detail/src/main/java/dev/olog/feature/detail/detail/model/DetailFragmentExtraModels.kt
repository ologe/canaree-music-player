package dev.olog.feature.detail.detail.model

import dev.olog.domain.mediaid.MediaId

internal data class DetailFragmentMostPlayedModel(
    val mediaId: MediaId.Track,
    val title: String,
    val subtitle: String,
    val position: Int,
) {

    val formattedPosition: String
        get() = (position + 1).toString()

}

internal data class DetailFragmentRecentlyAddedModel(
    val mediaId: MediaId.Track,
    val title: String,
    val subtitle: String,
)
internal data class DetailFragmentAlbumModel(
    val mediaId: MediaId.Category,
    val title: String,
    val subtitle: String,
)

internal data class DetailFragmentRelatedArtistModel(
    val mediaId: MediaId.Category,
    val title: String,
    val subtitle: String,
)