package dev.olog.msc.music.service.model

data class PlayerMediaEntity(
        val mediaEntity: MediaEntity,
        val positionInQueue: PositionInQueue
)