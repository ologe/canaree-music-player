package dev.olog.presentation.model

import dev.olog.core.MediaId
import dev.olog.feature.media.api.DurationUtils

data class DisplayableQueueSong(
    override val type: Int,
    override val mediaId: MediaId,
    val title: String,
    val artist: String,
    val album: String,
    val idInPlaylist: Int,
    val relativePosition: String,
    val isCurrentSong: Boolean

) : BaseModel {

    val subtitle = "$artist${DurationUtils.MIDDLE_DOT_SPACED}$album"

}