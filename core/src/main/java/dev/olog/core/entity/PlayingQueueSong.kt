package dev.olog.core.entity

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song

class PlayingQueueSong(
    @JvmField
    val song: Song,
    @JvmField
    val mediaId: MediaId
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayingQueueSong

        if (song != other.song) return false
        if (mediaId != other.mediaId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = song.hashCode()
        result = 31 * result + mediaId.hashCode()
        return result
    }
}