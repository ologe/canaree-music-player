package dev.olog.core.entity

import dev.olog.core.track.Song

data class MostPlayedSong(
    val song: Song,
    val counter: Int,
)