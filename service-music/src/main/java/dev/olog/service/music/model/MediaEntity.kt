package dev.olog.service.music.model

import dev.olog.domain.entity.PlayingQueueTrack
import dev.olog.domain.entity.track.Track
import dev.olog.domain.mediaid.MediaId
import kotlin.time.Duration
import kotlin.time.milliseconds

// TODO delete unused fields
internal data class MediaEntity(
    val progressive: Int,
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Duration,
) {

    val isPodcast: Boolean
        get() = mediaId.isAnyPodcast
    val mediaId: MediaId.Track
        get() = MediaId.songId(this.id)

    companion object {
        val EMPTY: MediaEntity
            get() = MediaEntity(
                progressive = 0,
                id = 0,
                title = "",
                artist = "",
                album = "",
                duration = 0.milliseconds,
            )
    }

}

internal fun Track.toMediaEntity(progressive: Int) : MediaEntity {
    return MediaEntity(
        progressive = progressive,
        id = this.id,
        title = this.title,
        artist = this.artist,
        album = this.album,
        duration = this.duration.milliseconds,
    )
}

internal fun PlayingQueueTrack.toMediaEntity() : MediaEntity {
    val track = this.track
    return MediaEntity(
        progressive = serviceProgressive,
        id = track.id,
        title = track.title,
        artist = track.artist,
        album = track.album,
        duration = track.duration.milliseconds,
    )
}