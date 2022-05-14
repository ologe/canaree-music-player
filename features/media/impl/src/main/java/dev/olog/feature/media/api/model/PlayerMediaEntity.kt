package dev.olog.feature.media.api.model

internal data class PlayerMediaEntity(
    val mediaEntity: MediaEntity,
    val positionInQueue: PositionInQueue,
    val bookmark: Long
)