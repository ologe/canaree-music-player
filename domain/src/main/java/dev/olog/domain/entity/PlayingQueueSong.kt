package dev.olog.domain.entity

import dev.olog.domain.MediaId
import dev.olog.domain.entity.track.Song

data class PlayingQueueSong(
    val song: Song,
    val mediaId: MediaId.Track
)