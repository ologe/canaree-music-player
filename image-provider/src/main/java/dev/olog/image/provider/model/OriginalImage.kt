package dev.olog.image.provider.model

import dev.olog.core.MediaId

class OriginalImage(
    @JvmField
    val mediaId: MediaId
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OriginalImage

        if (mediaId != other.mediaId) return false

        return true
    }

    override fun hashCode(): Int {
        return mediaId.hashCode()
    }
}