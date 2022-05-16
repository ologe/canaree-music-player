package dev.olog.feature.queue

import dev.olog.core.MediaId

data class QueueItem(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
    val idInPlaylist: Int,
    // position in queue relative to playing track
    val relativePosition: String,
    val isCurrentSong: Boolean,
)