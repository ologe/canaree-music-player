package dev.olog.core.entity

import dev.olog.core.MediaId

class SearchResult(
    @JvmField
    val mediaId: MediaId,
    @JvmField
    val itemType: Int,
    @JvmField
    val title: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SearchResult

        if (mediaId != other.mediaId) return false
        if (itemType != other.itemType) return false
        if (title != other.title) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mediaId.hashCode()
        result = 31 * result + itemType
        result = 31 * result + title.hashCode()
        return result
    }
}