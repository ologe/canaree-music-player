package dev.olog.service.music.model

internal class MetadataEntity(
    @JvmField
    val entity: MediaEntity,
    @JvmField
    val skipType: SkipType
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MetadataEntity

        if (entity != other.entity) return false
        if (skipType != other.skipType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = entity.hashCode()
        result = 31 * result + skipType.hashCode()
        return result
    }
}