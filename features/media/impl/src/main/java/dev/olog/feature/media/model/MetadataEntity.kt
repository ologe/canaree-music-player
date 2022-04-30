package dev.olog.feature.media.model

internal data class MetadataEntity(
    val entity: MediaEntity,
    val skipType: SkipType
)