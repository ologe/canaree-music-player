package dev.olog.service.music.model

import kotlin.time.Duration

internal data class PlayerMediaEntity(
    val mediaEntity: MediaEntity,
    val positionInQueue: PositionInQueue,
    val skipType: SkipType,
    val bookmark: Duration
)