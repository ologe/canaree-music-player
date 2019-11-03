package dev.olog.core.entity

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song

data class PlayingQueueSong(
    val song: Song,
    val mediaId: MediaId
)