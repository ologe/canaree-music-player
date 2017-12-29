package dev.olog.music_service.model

import dev.olog.domain.entity.PlayingQueueSong
import dev.olog.domain.entity.Song
import dev.olog.shared.MediaId

data class MediaEntity(
        val id: Long,
        val mediaId: MediaId,
        val title: String,
        val artist: String,
        val album: String,
        val image: String,
        val duration: Long,
        val isRemix: Boolean,
        val isExplicit: Boolean
)

fun Song.toMediaEntity(mediaId: MediaId) : MediaEntity {
    return MediaEntity(
            this.id,
            MediaId.playableItem(mediaId, this.id),
            this.title,
            this.artist,
            this.album,
            this.image,
            this.duration,
            this.isRemix,
            this.isExplicit
    )
}

fun PlayingQueueSong.toMediaEntity() : MediaEntity {
    return MediaEntity(
            this.id,
            MediaId.playableItem(parentMediaId, this.id),
            this.title,
            this.artist,
            this.album,
            this.image,
            this.duration,
            this.isRemix,
            this.isExplicit
    )
}

fun MediaEntity.toPlayerMediaEntity(positionInQueue: PositionInQueue) : PlayerMediaEntity {
    return PlayerMediaEntity(this, positionInQueue)
}