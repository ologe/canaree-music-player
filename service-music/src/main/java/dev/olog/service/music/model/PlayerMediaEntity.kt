package dev.olog.service.music.model

import kotlin.time.Duration

internal data class PlayerMediaEntity(
    val mediaEntity: MediaEntity,
    val positionInQueue: PositionInQueue,
    val skipType: SkipType,
    val bookmark: Duration
)

internal fun MediaEntity.toPlayerMediaEntity(
    positionInQueue: PositionInQueue,
    bookmark: Duration,
    skipType: SkipType,
) : PlayerMediaEntity {
    return PlayerMediaEntity(
        mediaEntity = this,
        positionInQueue = positionInQueue,
        bookmark = bookmark,
        skipType = skipType,
    )
}