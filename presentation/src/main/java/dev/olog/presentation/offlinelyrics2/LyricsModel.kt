package dev.olog.presentation.offlinelyrics2

import dev.olog.core.MediaId
import dev.olog.presentation.model.BaseModel

class LyricsModel(
    override val type: Int,
    override val mediaId: MediaId,
    @JvmField
    val content: String,
    @JvmField
    val isCurrent: Boolean
) : BaseModel {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LyricsModel

        if (type != other.type) return false
        if (mediaId != other.mediaId) return false
        if (content != other.content) return false
        if (isCurrent != other.isCurrent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + mediaId.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + isCurrent.hashCode()
        return result
    }
}