package dev.olog.lib.media.model

import dev.olog.core.mediaid.MediaId

data class PlayerItem(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
    val serviceProgressive: Long
)