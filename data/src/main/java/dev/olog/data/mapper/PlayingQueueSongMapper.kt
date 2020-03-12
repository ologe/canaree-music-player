package dev.olog.data.mapper

import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.entity.track.Song

internal fun Song.toPlayingQueueSong(progressive: Int): PlayingQueueSong {
    return PlayingQueueSong(
        song = this.copy(idInPlaylist = progressive),
        mediaId = mediaId
    )
}