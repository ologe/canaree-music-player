package dev.olog.core.entity

import dev.olog.core.entity.track.Track

data class PlayingQueueTrack(
    val track: Track,
    val serviceProgressive: Int,
)