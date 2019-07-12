package dev.olog.service.music.model

import dev.olog.core.MediaId
import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.entity.track.Song

data class MediaEntity(
    val id: Long,
    val idInPlaylist: Int,
    val mediaId: MediaId,
    val artistId: Long,
    val albumId: Long,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val album: String,
    val duration: Long,
    val dateAdded: Long,
    val path: String,
    val folder: String,
    val discNumber: Int,
    val trackNumber: Int,
    val isPodcast: Boolean
)

fun Song.toMediaEntity(progressive: Int, mediaId: MediaId) : MediaEntity {
    return MediaEntity(
        this.id,
        progressive,
        MediaId.playableItem(mediaId, this.id),
        this.artistId,
        this.albumId,
        this.title,
        this.artist,
        this.albumArtist,
        this.album,
        this.duration,
        this.dateAdded,
        this.path,
        this.folder,
        this.discNumber,
        this.trackNumber,
        this.isPodcast
    )
}

fun PlayingQueueSong.toMediaEntity() : MediaEntity {
    return MediaEntity(
        this.id,
        this.idInPlaylist,
        MediaId.playableItem(parentMediaId, this.id),
        this.artistId,
        this.albumId,
        this.title,
        this.artist,
        this.albumArtist,
        this.album,
        this.duration,
        this.dateAdded,
        this.path,
        this.folder,
        this.discNumber,
        this.trackNumber,
        this.isPodcast
    )
}

fun MediaEntity.toPlayerMediaEntity(positionInQueue: PositionInQueue, bookmark: Long) : PlayerMediaEntity {
    return PlayerMediaEntity(this, positionInQueue, bookmark)
}