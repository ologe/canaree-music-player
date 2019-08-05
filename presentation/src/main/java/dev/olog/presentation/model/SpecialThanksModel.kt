package dev.olog.presentation.model

import dev.olog.core.MediaId

class SpecialThanksModel(
    override val type: Int,
    override val mediaId: MediaId,
    @JvmField
    val title: String,
    @JvmField
    val image: Int
) : BaseModel {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SpecialThanksModel

        if (type != other.type) return false
        if (mediaId != other.mediaId) return false
        if (title != other.title) return false
        if (image != other.image) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + mediaId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + image
        return result
    }
}