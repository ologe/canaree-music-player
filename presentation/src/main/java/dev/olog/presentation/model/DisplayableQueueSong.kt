package dev.olog.presentation.model

import dev.olog.feature.presentation.base.model.BaseModel
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.shared.TextUtils

data class DisplayableQueueSong(
    override val type: Int,
    override val mediaId: PresentationId.Track,
    val title: String,
    val artist: String,
    val album: String,
    val idInPlaylist: Int,
    val relativePosition: String,
    val isCurrentSong: Boolean

) : BaseModel {

    val subtitle = "$artist${TextUtils.MIDDLE_DOT_SPACED}$album"

}