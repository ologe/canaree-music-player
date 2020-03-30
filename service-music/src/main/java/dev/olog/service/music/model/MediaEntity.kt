package dev.olog.service.music.model

import android.net.Uri
import dev.olog.core.MediaId
import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.entity.track.Song

internal data class MediaEntity(
    val id: Long,
    val idInPlaylist: Int,
    val mediaId: MediaId.Track,
    val artistId: Long,
    val albumId: Long,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val album: String,
    val duration: Long,
    val dateAdded: Long,
    val path: String,
    val discNumber: Int,
    val trackNumber: Int,
    val isPodcast: Boolean,
    val previewUrl: Uri
)

internal fun Song.toMediaEntity(progressive: Int, mediaId: MediaId.Category) : MediaEntity {
    return MediaEntity(
        this.id,
        progressive,
        mediaId.playableItem(this.id),
        this.artistId,
        this.albumId,
        this.title,
        this.artist,
        this.albumArtist,
        this.album,
        this.duration,
        this.dateAdded,
        this.path,
        this.discNumber,
        this.trackNumber,
        this.isPodcast,
        Uri.EMPTY
    )
}

internal fun PlayingQueueSong.toMediaEntity() : MediaEntity {
    val song = this.song
    return MediaEntity(
        song.id,
        song.idInPlaylist,
        this.mediaId,
        song.artistId,
        song.albumId,
        song.title,
        song.artist,
        song.albumArtist,
        song.album,
        song.duration,
        song.dateAdded,
        song.path,
        song.discNumber,
        song.trackNumber,
        song.isPodcast,
        Uri.EMPTY
    )
}

internal fun MediaEntity.toPlayerMediaEntity(positionInQueue: PositionInQueue, bookmark: Long) : PlayerMediaEntity {
    return PlayerMediaEntity(this, positionInQueue, bookmark)
}