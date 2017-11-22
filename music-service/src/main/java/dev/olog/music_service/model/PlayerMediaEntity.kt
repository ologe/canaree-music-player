package dev.olog.music_service.model

data class PlayerMediaEntity(
        val mediaEntity: MediaEntity,
        val positionInQueue: PositionInQueue
)