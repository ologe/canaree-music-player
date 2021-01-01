package dev.olog.feature.queue

import dev.olog.domain.mediaid.MediaId

data class PlayingQueueFragmentModel(
    val mediaId: MediaId,
    val serviceProgressive: Long,
    val title: String,
    val subtitle: String,
    val relativePosition: String,
    val isCurrentSong: Boolean,
)