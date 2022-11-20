package dev.olog.image.provider.loader

import com.bumptech.glide.load.Key
import dev.olog.core.MediaId
import java.security.MessageDigest

// check on category and id and most importantly on [albumIds] to allow creating new images when
// content changes
internal class MergedImageKey(
    private val albumIds: Set<Long>,
    mediaId: MediaId,
) : Key {

    private val category = mediaId.category
    private val id = mediaId.categoryValue


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MergedImageKey

        if (albumIds != other.albumIds) return false
        if (category != other.category) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = albumIds.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

    override fun toString(): String {
        return "MergedImageKey(albumIds=$albumIds, category=$category, id='$id')"
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(this.toString().toByteArray(Key.CHARSET))
    }
}