package dev.olog.feature.queue

import dev.olog.core.MediaId

data class PlayingQueueFragmentModel(
    val mediaId: MediaId,
    val serviceProgressive: Long,
    val title: String,
    val subtitle: String,
    val relativePosition: String,
    val isCurrentSong: Boolean,
)