package dev.olog.service.music.model

internal data class MetadataEntity(
    val entity: MediaEntity,
    val skipType: SkipType
)