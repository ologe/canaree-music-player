package dev.olog.feature.service.music.model

internal data class MetadataEntity(
    val entity: MediaEntity,
    val skipType: SkipType
)