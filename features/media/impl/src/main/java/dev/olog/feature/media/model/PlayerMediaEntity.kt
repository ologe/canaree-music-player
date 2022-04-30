package dev.olog.feature.media.model

internal data class PlayerMediaEntity(
    val mediaEntity: MediaEntity,
    val positionInQueue: PositionInQueue,
    val bookmark: Long
)