package dev.olog.feature.detail.detail.model

import dev.olog.core.MediaId
import dev.olog.presentation.model.BaseModel

// R.layout.item_detail_song_most_played
internal data class DetailFragmentMostPlayedModel(
    override val mediaId: MediaId,
    val title: String,
    val subtitle: String,
    val position: Int,
) : BaseModel {

    val formattedPosition: String
        get() = (position + 1).toString()

    override val type: Int
        get() = TODO("Not yet implemented")
}

internal data class DetailFragmentRecentlyAddedModel(
    override val mediaId: MediaId,
    val title: String,
    val subtitle: String,
): BaseModel {
    override val type: Int
        get() = TODO("Not yet implemented")
}

internal data class DetailFragmentAlbumModel(
    override val mediaId: MediaId,
    val title: String,
    val subtitle: String,
): BaseModel {
    override val type: Int
        get() = TODO("Not yet implemented")
}

internal data class DetailFragmentRelatedArtistModel(
    override val mediaId: MediaId,
    val title: String,
    val subtitle: String,
) : BaseModel {
    override val type: Int
        get() = TODO("Not yet implemented")
}