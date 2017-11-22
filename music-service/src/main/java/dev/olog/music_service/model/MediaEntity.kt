package dev.olog.music_service.model

import dev.olog.domain.entity.Song

data class MediaEntity(
        val id: Long,
        val title: String,
        val artist: String,
        val album: String,
        val image: String,
        val duration: Long,
        val isRemix: Boolean,
        val isExplicit: Boolean
)

fun Song.toMediaEntity() : MediaEntity {
    return MediaEntity(
            this.id,
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