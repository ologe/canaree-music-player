package dev.olog.lib.mapper

import dev.olog.domain.entity.PlayingQueueSong
import dev.olog.domain.entity.track.Song

internal fun Song.toPlayingQueueSong(progressive: Int): PlayingQueueSong {
    return PlayingQueueSong(
        song = this.copy(idInPlaylist = progressive),
        mediaId = mediaId
    )
}