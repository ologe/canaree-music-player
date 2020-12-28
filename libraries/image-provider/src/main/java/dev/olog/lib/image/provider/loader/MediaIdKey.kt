package dev.olog.lib.image.provider.loader

import com.bumptech.glide.load.Key
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import java.security.MessageDigest

internal class MediaIdKey(
    private val mediaId: MediaId
) : Key {

    override fun toString(): String {
        if (mediaId.isLeaf) {
            return "${MediaIdCategory.SONGS}-${mediaId.leaf}"
        }
        return "${mediaId.category.name}-${mediaId.categoryValue}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediaIdKey

        if (this.mediaId.isLeaf && other.mediaId.isLeaf) {
            // is track
            return this.mediaId.leaf == other.mediaId.leaf
        }
        return this.mediaId.category == other.mediaId.category &&
                this.mediaId.categoryValue == other.mediaId.categoryValue
    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + mediaId.category.name.hashCode()
        result = 31 * result + mediaId.categoryValue.hashCode()
        if (mediaId.isLeaf) {
            result = 31 * result + mediaId.leaf!!.hashCode()
        }
        return result
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(this.toString().toByteArray(Key.CHARSET))
    }

}
