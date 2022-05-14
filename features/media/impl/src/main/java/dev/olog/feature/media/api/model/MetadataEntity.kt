package dev.olog.feature.media.api.model

internal data class MetadataEntity(
    val entity: MediaEntity,
    val skipType: SkipType
)