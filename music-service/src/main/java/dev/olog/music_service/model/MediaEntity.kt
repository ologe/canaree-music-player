package dev.olog.music_service.model

import dev.olog.domain.entity.PlayingQueueSong
import dev.olog.domain.entity.Song
import dev.olog.shared.MediaId

data class MediaEntity(
        val id: Long,
        val mediaId: MediaId,
        val artistId: Long,
        val albumId: Long,
        val title: String,
        val artist: String,
        val album: String,
        val image: String,
        val duration: Long,
        val dateAdded: Long,
        val isRemix: Boolean,
        val isExplicit: Boolean,
        val path: String,
        val trackNumber: Int,
        val idInPlaylist: Int
)

fun Song.toMediaEntity(progressive: Int, mediaId: MediaId) : MediaEntity {
    return MediaEntity(
            this.id,
            MediaId.playableItem(mediaId, this.id),
            this.artistId,
            this.albumId,
            this.title,
            this.artist,
            this.album,
            this.image,
            this.duration,
            this.dateAdded,
            this.isRemix,
            this.isExplicit,
            this.path,
            this.trackNumber,
            progressive
    )
}

fun PlayingQueueSong.toMediaEntity() : MediaEntity {
    return MediaEntity(
            this.id,
            MediaId.playableItem(parentMediaId, this.id),
            this.artistId,
            this.albumId,
            this.title,
            this.artist,
            this.album,
            this.image,
            this.duration,
            this.dateAdded,
            this.isRemix,
            this.isExplicit,
            this.path,
            this.trackNumber,
            this.idInPlaylist
    )
}

fun MediaEntity.toPlayerMediaEntity(positionInQueue: PositionInQueue) : PlayerMediaEntity {
    return PlayerMediaEntity(this, positionInQueue)
}