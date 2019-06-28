package dev.olog.service.music.model

data class PlayerMediaEntity(
    val mediaEntity: MediaEntity,
    val positionInQueue: PositionInQueue,
    val bookmark: Long
)