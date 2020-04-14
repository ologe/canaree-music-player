package dev.olog.lib.media.model

import dev.olog.domain.MediaId

data class PlayerItem(
    val mediaId: MediaId,
    val title: String,
    val artist: String,
    val idInPlaylist: Long
)