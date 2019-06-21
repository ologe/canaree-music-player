package dev.olog.image.provider.loader

import com.bumptech.glide.load.Key
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import java.security.MessageDigest

internal class MediaIdKey(private val mediaId: MediaId) : Key {

    override fun toString(): String {
        if (mediaId.isLeaf) {
            return "${MediaIdCategory.SONGS}${mediaId.leaf}"
        }
        return "${mediaId.category}${mediaId.categoryValue}"
    }

    override fun equals(other: Any?): Boolean {
        if (other is MediaId) {
            if (this.mediaId.isLeaf && other.isLeaf) {
                // is song
                return this.mediaId.leaf == other.leaf
            }
            return this.mediaId.category == other.category &&
                    this.mediaId.categoryValue == other.categoryValue
        }
        return false
    }

    override fun hashCode(): Int {
        if (mediaId.isLeaf) {
            var result = MediaIdCategory.SONGS.hashCode()
            result = 31 * result + mediaId.leaf!!.hashCode()
            return result
        }
        var result = mediaId.category.hashCode()
        result = 31 * result + mediaId.categoryValue.hashCode()
        return result
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(this.toString().toByteArray(Key.CHARSET))
    }

}
