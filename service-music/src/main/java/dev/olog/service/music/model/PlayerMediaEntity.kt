package dev.olog.service.music.model

internal data class PlayerMediaEntity(
    val mediaEntity: MediaEntity,
    val positionInQueue: PositionInQueue,
    val skipType: SkipType,
    val bookmark: Long
)