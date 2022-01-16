package dev.olog.core.queue

import dev.olog.core.track.Song

data class PlayingQueueSong(
    val song: Song,
    val playOrder: Int,
) {

    companion object

}