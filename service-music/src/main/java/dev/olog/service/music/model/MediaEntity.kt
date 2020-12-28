package dev.olog.service.music.model

import dev.olog.core.MediaId
import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.entity.track.Song
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

internal fun Song.toMediaEntity(progressive: Int, mediaId: MediaId) : MediaEntity {
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

internal fun PlayingQueueSong.toMediaEntity() : MediaEntity {
    val song = this.song
    return MediaEntity(
        progressive = serviceProgressive,
        id = song.id,
        mediaId = song.getMediaId(),
        artistId = song.artistId,
        albumId = song.albumId,
        title = song.title,
        artist = song.artist,
        albumArtist = song.albumArtist,
        album = song.album,
        duration = song.duration.milliseconds,
        dateAdded = song.dateAdded,
        path = song.path,
        discNumber = song.discNumber,
        trackNumber = song.trackNumber,
        isPodcast = song.isPodcast
    )
}