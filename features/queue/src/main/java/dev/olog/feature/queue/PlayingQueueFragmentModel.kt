package dev.olog.feature.queue

import dev.olog.core.MediaId

data class PlayingQueueFragmentModel(
    val mediaId: MediaId, // track media id
    val progressive: Int, // id in playing queue
    val title: String,
    val subtitle: String,
    val relativePosition: String,
    val isCurrentSong: Boolean,

)