package dev.olog.service.music.model

import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.entity.PlayingQueueTrack
import dev.olog.domain.entity.track.Track
import kotlin.time.Duration
import kotlin.time.milliseconds

internal data class MediaEntity(
    val progressive: Int,
    val id: Long,
    val mediaId: MediaId,
    val artistId: Long,
    val albumId: Long,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val album: String,
    val duration: Duration,
    val dateAdded: Long,
    val path: String,
    val discNumber: Int,
    val trackNumber: Int,
    val isPodcast: Boolean
) {

    companion object {
        val EMPTY: MediaEntity
            get() = MediaEntity(
                progressive = 0,
                id = 0,
                mediaId = MediaId.songId(0),
                artistId = 0,
                albumId = 0,
                title = "",
                artist = "",
                album = "",
                albumArtist = "",
                duration = 0.milliseconds,
                dateAdded = 0,
                path = "",
                discNumber = 0,
                trackNumber = 0,
                isPodcast = false
            )
    }

}

internal fun Track.toMediaEntity(progressive: Int, mediaId: MediaId) : MediaEntity {
    return MediaEntity(
        progressive = progressive,
        id = this.id,
        mediaId = MediaId.playableItem(mediaId, this.id),
        artistId = this.artistId,
        albumId = this.albumId,
        title = this.title,
        artist = this.artist,
        albumArtist = this.albumArtist,
        album = this.album,
        duration = this.duration.milliseconds,
        dateAdded = this.dateAdded,
        path = this.path,
        discNumber = this.discNumber,
        trackNumber = this.trackNumber,
        isPodcast = this.isPodcast
    )
}

internal fun PlayingQueueTrack.toMediaEntity() : MediaEntity {
    val track = this.track
    return MediaEntity(
        progressive = serviceProgressive,
        id = track.id,
        mediaId = track.getMediaId(),
        artistId = track.artistId,
        albumId = track.albumId,
        title = track.title,
        artist = track.artist,
        albumArtist = track.albumArtist,
        album = track.album,
        duration = track.duration.milliseconds,
        dateAdded = track.dateAdded,
        path = track.path,
        discNumber = track.discNumber,
        trackNumber = track.trackNumber,
        isPodcast = track.isPodcast
    )
}