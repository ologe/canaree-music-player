package dev.olog.media.model

import dev.olog.core.MediaId

data class PlayerItem(
    val mediaId: MediaId,
    val title: String,
    val artist: String,
    val idInPlaylist: Long
)