package dev.olog.domain.entity

import dev.olog.domain.entity.track.Track

data class PlayingQueueTrack(
    val track: Track,
    val serviceProgressive: Int,
)